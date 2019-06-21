var config = {
	apiKey: "AIzaSyB-oHbnaf1DwSQf1wzkBnVfdqGOp8XdBYY",
	authDomain: "notificationproject-d2ce4.firebaseapp.com",
	databaseURL: "https://notificationproject-d2ce4.firebaseio.com",
	projectId: "notificationproject-d2ce4",
	storageBucket: "notificationproject-d2ce4.appspot.com",
	messagingSenderId: "597812487156",
	appId: "1:597812487156:web:f2d047a63f1a833f"
};

firebase.initializeApp(config);

firebase.auth.Auth.Persistence.LOCAL; 

$("#btn-login").click(function(){        
    var email = $("#email").val();
    var password = $("#password").val(); 

    var result = firebase.auth().signInWithEmailAndPassword(email, password);
    
    result.catch(function(error){
        var errorCode = error.code; 
        var errorMessage = error.message; 

        console.log(errorCode);
        console.log(errorMessage);
    });
});
