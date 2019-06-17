/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
var app = {
    // Application Constructor
    initialize: function() {
        document.addEventListener('deviceready', this.onDeviceReady.bind(this), false);
    },

    // deviceready Event Handler
    //
    // Bind any cordova events here. Common events are:
    // 'pause', 'resume', etc.
 onDeviceReady: function() {
         this.receivedEvent('deviceready');

         //create member call from cordova
//         token.createMember(
//                            {'mobileNumber':'+97333263334'},
//                            function(msg) {
////                            console.log("================")
//                            console.log(msg);
////                                    alert(msg);
//                            },
//                            function(msg) {
//                            console.log("failure="+msg);
//
//                            }
//                            );

 //        //subscription call from cordova
//         token.subscribe(
//                         {'memberId':'m:FkubFoTtYcFrW1S7f6nBNeWs5F9:5zKtXEAq'},
//                            function(msg) {
//                            console.log("success="+msg);
//                            },
//                            function(msg) {
//                            console.log("failure="+msg);
//
//                            }
//                            );


        // link accounts call - pass AT
//         token.linkAccounts(
//                            {'memberId':'m:FkubFoTtYcFrW1S7f6nBNeWs5F9:5zKtXEAq','accessToken':'lQjtLgWR2Y73GhEHQ+CAvM4MHFioOH3n9PZbcBOu1qaeESTNA9rLGWdMMBsO8iZmMSS0nRuz5+Ogw30Hg2U4BlUp55nb6w7Q8Ap1Ec0rQNv12ppWUaVHYAZcnlabnyf7Ilh4YM93p/vgQm3ujhX6eHFk8S/ZR9yl8L4tyQWRPEySB3BDnPXjrVvaKZfBq8vK30zQFpvG6VC77MeJeu0WOCpVb3/LGUW/OtXPNFZBzOMDL6fupkbK6YP7fuuWD82ZJC+67SwSNcM7W+Dnm3FIDoDEFduwqwB3qBsjikp5zrYUMUXvZE2o5sPES9ZyrpgCHCMPHxjnb8ybcdlNrJG4/fu+MkMWnxYJAIyn7jtahghYgU9pGLEdZmYUJQdODWcvyz5liZa6dxSGOEpTUHZXZMLpYuKWts382WMoPjSSZu2OtDhwvfI4/bCle7XHIeCcMQnM0PfcC/ECPNsgzYEuyH82clpEWpCtl2rOUG7DuWgne97LHybGlKknDS8WMgd+31O2DI8SvkQX9rqxjQiU01hTQXqd7Fshp1QqSfKsAj3l1MRCn0f3HOm0hf2jjZ1NvIY8w6hEHVnxOL26MhWIgsp7Q96v3MorEAgrZb0IWhYhiRJdXAqmyOD9NbWaOIuLRocLnt2UiuJMZXRdwnmIvsmP6omFBanSBIuDbou4e4n60VkrLjUsP0EK6JjeE9cU8/SxbzEjTIP8DCGHPwIDEk6WiZDbWJB3tze83uYc0zUl+1Zc1qs6mR6pVvofxssJLhBsUbm5L/MRZICKhPvyhqqfOU8qbyDHYrf0QfN+qAEj1HG36akhaFisCALB3/d4l4VI8VzqlLUiE7NhaVfjMN5nonm1gGXDBa4ynskeAMfFUZGyq+VD5aSTFlYAt+dAcUEpWEAiLkEM3WYCe1/BTyzhMc8rMNnHBxOdxCcDXRyUwR2QPX89rUxWohut0EzGBfGUw8qA8kOMnDhDhwkfKv4wHM4fkcfnhyLEBIMEVtl5wwDUPZmDqoUGJ1nvBsjZIREprRqipDEI84NAByIA/kFLCrNkj7W8FlL9qci0mBnYajbayUdTGC3mtFd33NDqEWD/IOzVYi8DlT2efGoZG0I2eFd3iubA/4P/+H+aM+lYOQwRYkO1ZAludjJK0d8PbX/FzkU4Hh+0XwHXoSYQnwL4jqZnrvxEKzt7wyMYyTfb1ASwLpac6bL+4xKS9zq72oJHnXH/eP4Fmgi88+r+OXbutsydkpIeOzor2S0a98aVi/vS/QdDjK9YfNK62Qzgt9NxFdSFPj5OvA5r95laLdk5ewaRg8U8fv86aBA1wY8j2QbcPZ8j0pooOLhUu6FbNEpA6Dn+y4aq9oeb01GxUA==|m:FkubFoTtYcFrW1S7f6nBNeWs5F9:5zKtXEAq'},
//                         function(msg) {
//                         console.log("success="+msg);
//                         },
//                         function(msg) {
//                         console.log("failure="+msg);
//
//                         }
//                         );


         //link accounts call - pass AT
         //        token.linkAccounts(
         //                           {'memberId':'m:kzPExkVTHWxscnQNhQCzD74Gdob:5zKtXEAq','accessToken':'UAi4bPyCjCTyyTAFQvHqpZ6icRdVwF9Q+4tOQAXWC6PPODsjlQ/FFQO7U19ozPwmHuyRuXbatRzj71Z0OfR3ABR9new7A13t81Ell1srdC6Gxlaj2OFO7fqDSiEXZN1/FVTfC8JqJKMF0qrFxBtN5kCiHRI7m1zHfUYHlKF8QHyAWQXJNukyZEJvqOxjM3YqZOvuXoAtgz9QZ8/LYo25FFyRU1e0MfX/4GhDzx9e0a4YrHC4dTsyS4BhLvovzQatweGi8fQwvZ1IJn0GlfS5MkjPtGTkKUvKKIkYipFRQLeHYXaR9oAD5i1t7NjqN5H42DuwNco1otK2qHhjZfuGXamQflCRvu1ZtNwvX6O5VS/MvEuBCTDxGYW/bq6qOmNVwiYXfbtzcpUOHxDQToy1yBmleiSzjd/6byCJhRaPcWBwX8y0Gr22nhsTQmVinLkgLlAcOODvO5CPR1ZQ0LlLRYK9p7d8h0P7az5e9+TTJJdhc9cEBngV2+v1fclNiYpoCrcIWetiUrASA62DkZcYyw9ei0SNppGUWM+l+Usi0Cvnoj6D2dJwMB8WbQFal41SoMxiFHFKWT2IqSXbPBxykSNMi3abK7SV5rpXlZPMlMFhzC36Kzh6TxKlXhQ/cCnDlQk4fNe9KBewyob+mCRRQgDsjy9szNxVMcwRHh9WMHmDPhf63Xs6nIjL0k+xkWBGygicFwrSyfx1hMSE3HbovxGydTxEU1veEpPyoDJxjqJFCRnxZeYD/oc3o7A7NolMa2ImSWLwgRheILAIlctn16Aiy+z/Hpe9zYAWVyKPdjbGri5nWQvLBkOnLjmo2vgjhTHixE9JMu3Gya37CTN1fCBErh0s5D7YXg47ybesxY8O4Z3GHn/H1dbpHuNaXvHsIMJslthvxRJyTJtlKMmVMP0ZE0IjNKZjel1xpsZgqLpLZDWPtKO2w95hgD7AUI96O0y/H1CKcMa0+HFjazjmMHoZ8EGw1vsz6MrFp9uK7SuzWEkNSBNVdAUWdXDTZ3q8Us/G8NOXDU1i+v1b6+J/pGCg0ZPOSBi6+JDGq1vpTEs1hlllfXWWeGtQKlKB91sPTJu+Lgvz5rK+d2q9nIA/t1FEJZSH0Qium0SZ8Ix1gw1illp+a+LjE85FoEbruAz2161xUNuWVVYwKAd2tncPrQgOJBtjHgO4PISFQoWH1sHMvlflkZBf5+J2GjhG1tknBhThdsaWUxB1+vHuMFGisUgl3Y6HVSax99F8pa1H4NOmqrSkmSOGyS34WuEQgk6jI+xoyMU9E03CfmyYyY9wWo/0BtROu1kOx0XSMhL1AQ/494ErrSveCCbw2JlruX3rYJ2BCvKG7aXxA42klN5I4w=='},
         //                        function(msg) {
         //                        console.log("success",msg);
         //                        },
         //                        function(msg) {
         //                        console.log("failure",msg);
         //
         //                        }
         //                        );


                 //get linked accounts
//                 token.getAccounts(
//                                   {'memberId':'m:FkubFoTtYcFrW1S7f6nBNeWs5F9:5zKtXEAq'},
//                                    function(msg) {
//                                    console.log("success="+msg);
//                                    },
//                                    function(msg) {
//                                    console.log("failure="+msg);
//                                    }
//                                    );


         //get account
//                         token.getAccount(
//                                           {'memberId':'m:FkubFoTtYcFrW1S7f6nBNeWs5F9:5zKtXEAq','tokenAccountId':'a:Gnv5qeBpdwGpLJmWQoo8RYhMz9UnaTqtG4DymvMsL7L5:8QRouC2tRGXx'},
//                                            function(msg) {
//                                            console.log("success",msg);
//                                            },
//                                            function(msg) {
//                                            console.log("failure",msg);
//
//                                            }
//                                            );


         //get aisp consents
 //        token.getConsents(
 //                          {'memberId':'m:2z4mMsE81YEKkq5Rk89ZUSgkYxV8:5zKtXEAq'},
 //                          function(msg) {
 //                          console.log("success",msg);
 //                          },
 //                          function(msg) {
 //                          console.log("failure",msg);
 //
 //                          }
 //                          );

 //        {'memberId':'m:2xX2GsxnRDE4SYYRWJw7yDHdzv3p:5zKtXEAq','accounts':['a:FYKyDCCKqpc6fhvNVCh4oBqhR5w4i3m6wn1FB6Vo3HuP:8QRouC2qXzi5','a:DDmcyTDdV9EjLac1WTAJeBU2uLsMnWetix42v6pjZSqp:8QRouC2qXzi5']'},
 //
 //

         //unlink accounts
//         token.unlinkAccounts(
//                           {'memberId':'m:FkubFoTtYcFrW1S7f6nBNeWs5F9:5zKtXEAq','accounts':['a:GvM5GE6LGYjVi8Qb4AmCzVVBCGAJsbkkm4UQfZ67Ex7Z:8QRouC2tRGXx']},
//                           function(msg) {
//                           console.log("success"+msg);
//                           },
//                           function(msg) {
//                           console.log("failure"+msg);
//
//                           }
//                           );


         //get transfers
 //        token.getTransfers(
 //                          {'memberId':'m:2z4mMsE81YEKkq5Rk89ZUSgkYxV8:5zKtXEAq'},
 //                          function(msg) {
 //                          console.log("success",msg);
 //                          },
 //                          function(msg) {
 //                          console.log("failure",msg);
 //
 //                          }
 //                          );


         //get profile
//                 token.getProfile(
//                                  {'memberId':'m:FkubFoTtYcFrW1S7f6nBNeWs5F9:5zKtXEAq','tppMemberId':'m:9Eoc6Yda9881vKSMLTUkRViWzju:5zKtXEAq'},
//                                   function(msg) {
//                                   console.log("success"+msg);
//                                   },
//                                   function(msg) {
//                                   console.log("failure"+msg);
//
//                                   }
//                                   );

        //get profile picture
//         token.getProfilePicture(
//                          {'memberId':'m:FkubFoTtYcFrW1S7f6nBNeWs5F9:5zKtXEAq','tppMemberId':'m:9Eoc6Yda9881vKSMLTUkRViWzju:5zKtXEAq'},
//                          function(msg) {
//                          console.log("success",msg);
//                          },
//                          function(msg) {
//                          console.log("failure",msg);
//
//                          }
//                          );
 //
         //delete member
//         token.deleteMember(
//                                 {'memberId':'m:kzPExkVTHWxscnQNhQCzD74Gdob:5zKtXEAq'},
//                                 function(msg) {
//                                 console.log("success",msg);
//                                 },
//                                 function(msg) {
//                                 console.log("failure",msg);
//
//                                 }
//                                 );

         //cancel access token
 //                token.cancelAccessToken(
 //                                        {'memberId':'m:2z4mMsE81YEKkq5Rk89ZUSgkYxV8:5zKtXEAq','tppMemberId':'m:ChPoyz39fJzTgH8TT7ApNiKHXeS:5zKtXEAq'},
 //                                        function(msg) {
 //                                        console.log("success",msg);
 //                                        },
 //                                        function(msg) {
 //                                        console.log("failure",msg);
 //
 //                                        }
 //                                        );
     },
    // Update DOM on a Received Event
    receivedEvent: function(id) {
        var parentElement = document.getElementById(id);
        var listeningElement = parentElement.querySelector('.listening');
        var receivedElement = parentElement.querySelector('.received');

        listeningElement.setAttribute('style', 'display:none;');
        receivedElement.setAttribute('style', 'display:block;');

        console.log('Received Event: ' + id);
    }
};

app.initialize();