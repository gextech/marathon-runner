// We require this because we don't have node's complete environment
var Rx;

try {
  Rx = require('rx');
} catch(e) {
  throw Error('Cannot load module rx, check it is in marathon path ' + e.message)
}

function fromJavaRx(javaObservable) {
  var Subscriber = Java.type("rx.Subscriber");
  return Rx.Observable.create(function (observer) {
    javaObservable.subscribe(new Subscriber({
      onCompleted: function() {
        observer.onCompleted();
      },
      onError: function(e) {
        observer.onError(e);
      },
      onNext: function(args) {
        observer.onNext(args);
      }
    }));
  });
}

function toJavaRx(jsObservable) {
  var Observable = Java.type("rx.Observable");
  var OnSubscribe = Observable.OnSubscribe;
  return Observable.create(new OnSubscribe({
    call: function(observer) {
      jsObservable.subscribe(
          function (args) {
            observer.onNext(args);
          },
          function (err) {
            observer.onNext(err);
          },
          function () {
            observer.onCompleted();
          });
    }
  }));
}

