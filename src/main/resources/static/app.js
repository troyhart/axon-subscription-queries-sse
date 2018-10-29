var basketModleEventStore = undefined;
var basketListingEventStore = undefined;
var currentBasketId = undefined;

function createBasket() {
  $.ajax({
    url : `/api/baskets`,
    type : 'POST',
    success : function(data) {
      $("#subscribe-to-basket-id").val(data);
      $(".lastbasketid").text(data);
      manageBasketViewModelUpdatesSubscription();
    },
    data : JSON.stringify({
      'type' : $("#basket-type").val()
    }),
    contentType : "application/json"
  });
}

function manageBasketViewModelUpdatesSubscription() {

  function unsubscribeFromBasketModel() {
    basketModleEventStore.close();
    basketModleEventStore = undefined;
  }

  if (basketModleEventStore) {
    unsubscribeFromBasketModel();
  }

  $("#updatelog").html("");
  currentBasketId = $("#subscribe-to-basket-id").val();
  $(".currentbasketid").text(currentBasketId);

  basketModleEventStore = new EventSourcePolyfill(
      `/api/baskets/${currentBasketId}`, {
        headers : {
          authorization : 'bearer my.token.value'
        }
      });
  var listener = function(event) {
    $("#updatelog")
        .prepend(
            basketViewMessage(event.type === "message" ? buildBasketViewModelMessage(JSON
                .parse(event.data))
                : basketModleEventStore.url)).prepend(
            basketViewMessageHeader(event.type));
  };
  basketModleEventStore.addEventListener("open", listener);
  basketModleEventStore.addEventListener("message", listener);
  basketModleEventStore.addEventListener("error", listener);
}

function buildBasketViewModelMessage(data) {
  var msg = `${data.type} :: ${data.id}`;
  if (data.things) {
    data.things.forEach(function(thing) {
      msg += '<ul>';
      msg += `<li>${thing.name} :: ${thing.description}</li>`;
      msg += '</ul>'
    });
  }
  return msg;
}

function basketViewMessageHeader(headerMessage) {
  return `<h4>${headerMessage}</h4>`;
}

function basketViewMessage(data) {
  return `<div>${data}</div>`;
}

function addThing() {
  $
      .ajax({
        url : `/api/baskets/${currentBasketId}/things`,
        type : 'PUT',
        success : function() {
          console
              .log("Successfully added the thing...expect basket view model update");
        },
        data : JSON.stringify({
          "name" : $("#thing-name").val(),
          "description" : $("#thing-description").val()
        }),
        contentType : "application/json"
      });
}

function listBasketsByType() {
  var typePartial = $("#basket-type-contains").val();
  manageBasketsByTypeListUpdatesSubscription(typePartial);
}

function manageBasketsByTypeListUpdatesSubscription(typePartial) {

  if (basketListingEventStore) {
    basketListingEventStore.close();
    basketListingEventStore = undefined;
  }

  $("#baskettypelisting").html("");
  basketListingEventStore = new EventSourcePolyfill(
      `/api/baskets?type=${typePartial}`, {
        headers : {
          authorization : 'bearer my.token.value'
        }
      });
  var listener = function(event) {
    var message;
    if (event.type === "message") {
      var data = JSON.parse(event.data);
      message = `<div data-basket-id="${data.id}" class="listedbasket">${data.type + " :: " + data.id}</div>`;
    } else {
      message = `<div>${basketListingEventStore.url}</div>`;
    }
    $("#baskettypelisting").prepend(message).prepend(`<h4>${event.type}</h4>`);
    $(".listedbasket").click(function(event) {
      $("#subscribe-to-basket-id").val($(event.target).attr("data-basket-id"));
      manageBasketViewModelUpdatesSubscription();
    });
  };
  basketListingEventStore.addEventListener("open", listener);
  basketListingEventStore.addEventListener("message", listener);
  basketListingEventStore.addEventListener("error", listener);
}

$(function() {

  $("form").on('submit', function(e) {
    e.preventDefault();
  });
  $("#create-basket").click(function() {
    createBasket();
  });
  $("#add-thing").click(function() {
    addThing();
  });
  $("#subscribe-to-basket").click(function() {
    manageBasketViewModelUpdatesSubscription();
  });
  $("#subscribe-to-basket-list-by-type").click(function(event) {
    listBasketsByType();
  });
});
