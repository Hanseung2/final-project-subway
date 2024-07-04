/*! For license information please see 347.3a848961.chunk.js.LICENSE.txt */
"use strict";(self.webpackChunkCapstone_Subway_TS=self.webpackChunkCapstone_Subway_TS||[]).push([[347],{9347:function(t,r,e){e.r(r);var n=e(4165),o=e(5861),i=e(9439),a=e(2791),c=e(9153),u=e(184);r.default=function(){var t=(0,a.useState)(null),r=(0,i.Z)(t,2),e=r[0],s=r[1],h=(0,a.useState)(!0),l=(0,i.Z)(h,2),f=l[0],p=l[1],v=(0,a.useState)(null),y=(0,i.Z)(v,2),d=y[0],g=y[1];return(0,a.useEffect)((function(){var t=function(){var t=(0,o.Z)((0,n.Z)().mark((function t(){var r,e;return(0,n.Z)().wrap((function(t){for(;;)switch(t.prev=t.next){case 0:return t.prev=0,"2","2312",t.next=5,fetch("https://apis.openapi.sk.com/puzzle/subway/congestion/rltm/trains/".concat("2","/").concat("2312"),{headers:{appkey:"5W2bkDqBrC4kWrij4ZizS5yuhe5U5mdI7bT3ZaGJ"}});case 5:if((r=t.sent).ok){t.next=8;break}throw new Error("API \uc694\uccad\uc5d0 \uc2e4\ud328\ud588\uc2b5\ub2c8\ub2e4.");case 8:return t.next=10,r.json();case 10:e=t.sent,s(e),t.next=17;break;case 14:t.prev=14,t.t0=t.catch(0),g(t.t0.message);case 17:return t.prev=17,p(!1),t.finish(17);case 20:case"end":return t.stop()}}),t,null,[[0,14,17,20]])})));return function(){return t.apply(this,arguments)}}();t()}),[]),(0,u.jsxs)(c.Z,{title:"\uc2e4\uc2dc\uac04 \uc5f4\ucc28 \ud63c\uc7a1\ub3c4",description:"\uc2e4\uc2dc\uac04 \uc5f4\ucc28 \ud63c\uc7a1\ub3c4 \ud398\uc774\uc9c0",children:[(0,u.jsx)("h1",{children:"\uc2e4\uc2dc\uac04 \uc5f4\ucc28 \ud63c\uc7a1\ub3c4"}),f?(0,u.jsx)("p",{children:"\ub370\uc774\ud130\ub97c \ubd88\ub7ec\uc624\ub294 \uc911\uc785\ub2c8\ub2e4..."}):d?(0,u.jsxs)("p",{children:["\uc624\ub958\uac00 \ubc1c\uc0dd\ud588\uc2b5\ub2c8\ub2e4: ",d]}):e&&e.success?(0,u.jsxs)("div",{children:[(0,u.jsxs)("p",{children:["\ud638\uc120: ",e.data.subwayLine]}),(0,u.jsxs)("p",{children:["\uc5f4\ucc28 \ubc88\ud638: ",e.data.trainY]}),(0,u.jsxs)("p",{children:["\uc5f4\ucc28 \ud63c\uc7a1\ub3c4: ",e.data.congestionResult.congestionTrain]}),(0,u.jsxs)("p",{children:["\uce78\ubcc4 \ud63c\uc7a1\ub3c4: ",e.data.congestionResult.congestionCar]})]}):(0,u.jsx)("p",{children:"\uc5f4\ucc28 \ud63c\uc7a1\ub3c4 \uc815\ubcf4\ub97c \uac00\uc838\uc624\uc9c0 \ubabb\ud588\uc2b5\ub2c8\ub2e4."})]})}},5861:function(t,r,e){function n(t,r,e,n,o,i,a){try{var c=t[i](a),u=c.value}catch(s){return void e(s)}c.done?r(u):Promise.resolve(u).then(n,o)}function o(t){return function(){var r=this,e=arguments;return new Promise((function(o,i){var a=t.apply(r,e);function c(t){n(a,o,i,c,u,"next",t)}function u(t){n(a,o,i,c,u,"throw",t)}c(void 0)}))}}e.d(r,{Z:function(){return o}})},4165:function(t,r,e){e.d(r,{Z:function(){return o}});var n=e(1002);function o(){o=function(){return r};var t,r={},e=Object.prototype,i=e.hasOwnProperty,a=Object.defineProperty||function(t,r,e){t[r]=e.value},c="function"==typeof Symbol?Symbol:{},u=c.iterator||"@@iterator",s=c.asyncIterator||"@@asyncIterator",h=c.toStringTag||"@@toStringTag";function l(t,r,e){return Object.defineProperty(t,r,{value:e,enumerable:!0,configurable:!0,writable:!0}),t[r]}try{l({},"")}catch(t){l=function(t,r,e){return t[r]=e}}function f(t,r,e,n){var o=r&&r.prototype instanceof m?r:m,i=Object.create(o.prototype),c=new G(n||[]);return a(i,"_invoke",{value:S(t,e,c)}),i}function p(t,r,e){try{return{type:"normal",arg:t.call(r,e)}}catch(t){return{type:"throw",arg:t}}}r.wrap=f;var v="suspendedStart",y="suspendedYield",d="executing",g="completed",w={};function m(){}function x(){}function b(){}var L={};l(L,u,(function(){return this}));var j=Object.getPrototypeOf,E=j&&j(j(N([])));E&&E!==e&&i.call(E,u)&&(L=E);var k=b.prototype=m.prototype=Object.create(L);function _(t){["next","throw","return"].forEach((function(r){l(t,r,(function(t){return this._invoke(r,t)}))}))}function O(t,r){function e(o,a,c,u){var s=p(t[o],t,a);if("throw"!==s.type){var h=s.arg,l=h.value;return l&&"object"==(0,n.Z)(l)&&i.call(l,"__await")?r.resolve(l.__await).then((function(t){e("next",t,c,u)}),(function(t){e("throw",t,c,u)})):r.resolve(l).then((function(t){h.value=t,c(h)}),(function(t){return e("throw",t,c,u)}))}u(s.arg)}var o;a(this,"_invoke",{value:function(t,n){function i(){return new r((function(r,o){e(t,n,r,o)}))}return o=o?o.then(i,i):i()}})}function S(r,e,n){var o=v;return function(i,a){if(o===d)throw new Error("Generator is already running");if(o===g){if("throw"===i)throw a;return{value:t,done:!0}}for(n.method=i,n.arg=a;;){var c=n.delegate;if(c){var u=Z(c,n);if(u){if(u===w)continue;return u}}if("next"===n.method)n.sent=n._sent=n.arg;else if("throw"===n.method){if(o===v)throw o=g,n.arg;n.dispatchException(n.arg)}else"return"===n.method&&n.abrupt("return",n.arg);o=d;var s=p(r,e,n);if("normal"===s.type){if(o=n.done?g:y,s.arg===w)continue;return{value:s.arg,done:n.done}}"throw"===s.type&&(o=g,n.method="throw",n.arg=s.arg)}}}function Z(r,e){var n=e.method,o=r.iterator[n];if(o===t)return e.delegate=null,"throw"===n&&r.iterator.return&&(e.method="return",e.arg=t,Z(r,e),"throw"===e.method)||"return"!==n&&(e.method="throw",e.arg=new TypeError("The iterator does not provide a '"+n+"' method")),w;var i=p(o,r.iterator,e.arg);if("throw"===i.type)return e.method="throw",e.arg=i.arg,e.delegate=null,w;var a=i.arg;return a?a.done?(e[r.resultName]=a.value,e.next=r.nextLoc,"return"!==e.method&&(e.method="next",e.arg=t),e.delegate=null,w):a:(e.method="throw",e.arg=new TypeError("iterator result is not an object"),e.delegate=null,w)}function P(t){var r={tryLoc:t[0]};1 in t&&(r.catchLoc=t[1]),2 in t&&(r.finallyLoc=t[2],r.afterLoc=t[3]),this.tryEntries.push(r)}function T(t){var r=t.completion||{};r.type="normal",delete r.arg,t.completion=r}function G(t){this.tryEntries=[{tryLoc:"root"}],t.forEach(P,this),this.reset(!0)}function N(r){if(r||""===r){var e=r[u];if(e)return e.call(r);if("function"==typeof r.next)return r;if(!isNaN(r.length)){var o=-1,a=function e(){for(;++o<r.length;)if(i.call(r,o))return e.value=r[o],e.done=!1,e;return e.value=t,e.done=!0,e};return a.next=a}}throw new TypeError((0,n.Z)(r)+" is not iterable")}return x.prototype=b,a(k,"constructor",{value:b,configurable:!0}),a(b,"constructor",{value:x,configurable:!0}),x.displayName=l(b,h,"GeneratorFunction"),r.isGeneratorFunction=function(t){var r="function"==typeof t&&t.constructor;return!!r&&(r===x||"GeneratorFunction"===(r.displayName||r.name))},r.mark=function(t){return Object.setPrototypeOf?Object.setPrototypeOf(t,b):(t.__proto__=b,l(t,h,"GeneratorFunction")),t.prototype=Object.create(k),t},r.awrap=function(t){return{__await:t}},_(O.prototype),l(O.prototype,s,(function(){return this})),r.AsyncIterator=O,r.async=function(t,e,n,o,i){void 0===i&&(i=Promise);var a=new O(f(t,e,n,o),i);return r.isGeneratorFunction(e)?a:a.next().then((function(t){return t.done?t.value:a.next()}))},_(k),l(k,h,"Generator"),l(k,u,(function(){return this})),l(k,"toString",(function(){return"[object Generator]"})),r.keys=function(t){var r=Object(t),e=[];for(var n in r)e.push(n);return e.reverse(),function t(){for(;e.length;){var n=e.pop();if(n in r)return t.value=n,t.done=!1,t}return t.done=!0,t}},r.values=N,G.prototype={constructor:G,reset:function(r){if(this.prev=0,this.next=0,this.sent=this._sent=t,this.done=!1,this.delegate=null,this.method="next",this.arg=t,this.tryEntries.forEach(T),!r)for(var e in this)"t"===e.charAt(0)&&i.call(this,e)&&!isNaN(+e.slice(1))&&(this[e]=t)},stop:function(){this.done=!0;var t=this.tryEntries[0].completion;if("throw"===t.type)throw t.arg;return this.rval},dispatchException:function(r){if(this.done)throw r;var e=this;function n(n,o){return c.type="throw",c.arg=r,e.next=n,o&&(e.method="next",e.arg=t),!!o}for(var o=this.tryEntries.length-1;o>=0;--o){var a=this.tryEntries[o],c=a.completion;if("root"===a.tryLoc)return n("end");if(a.tryLoc<=this.prev){var u=i.call(a,"catchLoc"),s=i.call(a,"finallyLoc");if(u&&s){if(this.prev<a.catchLoc)return n(a.catchLoc,!0);if(this.prev<a.finallyLoc)return n(a.finallyLoc)}else if(u){if(this.prev<a.catchLoc)return n(a.catchLoc,!0)}else{if(!s)throw new Error("try statement without catch or finally");if(this.prev<a.finallyLoc)return n(a.finallyLoc)}}}},abrupt:function(t,r){for(var e=this.tryEntries.length-1;e>=0;--e){var n=this.tryEntries[e];if(n.tryLoc<=this.prev&&i.call(n,"finallyLoc")&&this.prev<n.finallyLoc){var o=n;break}}o&&("break"===t||"continue"===t)&&o.tryLoc<=r&&r<=o.finallyLoc&&(o=null);var a=o?o.completion:{};return a.type=t,a.arg=r,o?(this.method="next",this.next=o.finallyLoc,w):this.complete(a)},complete:function(t,r){if("throw"===t.type)throw t.arg;return"break"===t.type||"continue"===t.type?this.next=t.arg:"return"===t.type?(this.rval=this.arg=t.arg,this.method="return",this.next="end"):"normal"===t.type&&r&&(this.next=r),w},finish:function(t){for(var r=this.tryEntries.length-1;r>=0;--r){var e=this.tryEntries[r];if(e.finallyLoc===t)return this.complete(e.completion,e.afterLoc),T(e),w}},catch:function(t){for(var r=this.tryEntries.length-1;r>=0;--r){var e=this.tryEntries[r];if(e.tryLoc===t){var n=e.completion;if("throw"===n.type){var o=n.arg;T(e)}return o}}throw new Error("illegal catch attempt")},delegateYield:function(r,e,n){return this.delegate={iterator:N(r),resultName:e,nextLoc:n},"next"===this.method&&(this.arg=t),w}},r}}}]);
//# sourceMappingURL=347.3a848961.chunk.js.map