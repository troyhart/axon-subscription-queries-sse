var eventSource = null;
var currentBasketId = null;

function createBasket(basketType) {
  $.ajax({
    url : `/api/baskets`,
    type : 'POST',
    success : function(data) {
      console.log("Created basket:......................", data);
      showBasketDetail(data);
    },
    data : JSON.stringify({
      'type' : basketType
    }),
    contentType : "application/json"
  });
}

function showBasketDetail(basketId) {
  $(".row.basketdetail").show();
  console.log('Showing basket details............................', basketId);
  if (!currentBasketId) {
    currentBasketId = basketId;
    subscribe(currentBasketId);
  } else if (basketId != currentBasketId) {
    unsubscribe();
    currentBasketId = basketId;
    subscribe(currentBasketId);
  }
  $("#currentbasketid").text(currentBasketId);
}

function subscribe(basketId) {
  eventSource = new EventSourcePolyfill(`/api/baskets/${basketId}`, {
    headers : {
      authorization: 'bearer my.token.value'
    }
  });
  var listener = function(event) {
    $("#updatelog")
        .prepend(
            `<div>${event.type === "message" ? event.data : eventSource.url}</div>`)
        .prepend(`<h3>${event.type}</h3>`);
  };
  eventSource.addEventListener("open", listener);
  eventSource.addEventListener("message", listener);
  eventSource.addEventListener("error", listener);
  $(".row.basketdetail").show();
}

function unsubscribe() {
  $(".row.basketdetail").hide();
  eventSource.close();
  eventSource = undefined;
}

function addThing() {
  console.log("Adding thing:......................", $("#thing-name").val(), $(
      "#thing-description").val());
  
  $.ajax({
    url : `/api/baskets/${currentBasketId}/things`,
    type : 'PUT',
    success : function() {
      console.log("Successfully added the thing...");
    },
    data : JSON.stringify({
      "name" : $("#thing-name").val(),
      "description" : $("#thing-description").val()
    }),
    contentType : "application/json"
  });
}

$(function() {
  $(".row.basketdetail").hide();
  $("form").on('submit', function(e) {
    e.preventDefault();
  });
  $("#create-basket").click(function() {
    createBasket($("#abstract-value").val());
  });
  $("#add-thing").click(function() {
    addThing();
  });
  $("#subscribe-to-basket").click(function(event) {
    showBasketDetail($("#abstract-value").val());
  });
});
