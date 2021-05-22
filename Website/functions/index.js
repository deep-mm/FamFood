var paytm_config = require('./paytm/paytm_config').paytm_config;
var paytm_checksum = require('./paytm/checksum');
var querystring = require('querystring');
const functions = require('firebase-functions');
const express = require('express');
const exphbs  = require('express-handlebars');
const engines = require('consolidate');

var firebase = require('firebase');
var bodyParser = require('body-parser');


var urlencodedParser = bodyParser.urlencoded({ extended: false })
var authenticated=false;
var authenticated_normal=false;
var uid;

const app = express();

var config = {
    apiKey: "AIzaSyDfqNXhVbc62NB5otyn_52679JUQ5Rrb64",
    authDomain: "cars-8af71.firebaseapp.com",
    databaseURL: "https://cars-8af71.firebaseio.com",
    projectId: "cars-8af71",
    storageBucket: "cars-8af71.appspot.com",
    messagingSenderId: "1000709043383"
    };
  firebase.initializeApp(config);
//app.use('/css',express.static('css'));

app.engine('hbs', engines.handlebars);

app.set('views','./views');
app.set('view engine', 'hbs');


app.get('/home',(request,response)=>{
  if(authenticated==true)
  {
    var user = firebase.auth().currentUser;
    if(user.uid==request.query.uid)
    {
  var fooditems=[];
    var price=[];

  var i=0;
  var ref1 =firebase.database().ref("/Menu/");
  ref1.on("value",function(snapshot2){


  var ref = firebase.database().ref("/Menu/canteen001/");
  ref.on("value",function(snapshot){
    snapshot.forEach(function(data){
      var ref1 = firebase.database().ref("/Menu/canteen001/"+data.key+"/itemEntities/");
      ref1.on("value",function(snapshot1){
        snapshot1.forEach(function(data1){
          var food = data1.val()
          fooditems[i]=food.itemName;
          price[i]=food.price;
          console.log(fooditems[i]+price[i]);
          i++;
        });
      },function(errorObject){
        console.log("error");
      });
    });

  },function(errorObject){
    console.log("error");
  });
  var data={fooditems:fooditems};
  var data1={price:price};
  console.log("kkk"+fooditems[2]);
  response.render("index4",{data:data,data1:data1});
},function(errorObject){
  console.log("error");
});
}
else {
  response.redirect('admin_login');
}
}
else {
  response.redirect('admin_login');
}
});

app.get('/Menu',function(req,res){
  if(authenticated==true)
  {
    var user = firebase.auth().currentUser;
    if(user.uid==req.query.uid)
    {
var category=[];

var i=0;
var ref1 =firebase.database().ref("/Menu/");
ref1.on("value",function(snapshot2){
var ref = firebase.database().ref("/Menu/canteen001/");
ref.once("value",function(snapshot){
  snapshot.forEach(function(data){
    console.log(data.key);
    category[i] = data.key;
    i++;
  });

},function(errorObject){
  console.log("error");
});


console.log("kkk"+category[1]);
var data={fooditems:category};
res.render("menu",{data:data});

},function(errorObject){
console.log("error");
});
}
else {
  res.redirect('admin_login');
}
}
else {
  res.redirect('admin_login');
}
});

app.get('/admin_login',function(req,res){
  res.render('login');
});

app.get('/',function(req,res){
  res.render('index');
});
app.get('/history',function(req,res){
  res.render('history');
});
app.get('/normal_login',function(req,res){
  res.render('normal_login');
});
app.get('/test',function(req,res){
  res.render('test');
});
app.post('/admin_login',urlencodedParser,function(req,res){
  if (!req.body) return res.sendStatus(400)
  console.log(req.body.email);
  firebase.auth().signInWithEmailAndPassword(req.body.email, req.body.password).then(function(user){
    authenticated =true;
    firebase.auth().onAuthStateChanged(function(user) {
  if (user) {
    // User is signed in.
    uid = user.uid;
    console.log("Before"+uid);
    res.redirect('home?uid='+uid);
  } else {
    res.render('admin_login');
    // No user is signed in.
  }
});
    console.log("after"+uid);

  }).catch(function(error) {
  res.end("Incorrect Credentials")
   console.log(error.code);
   console.log(error.message);
});
});

app.post('/normal_login',urlencodedParser,function(req,res){
  if (!req.body) return res.sendStatus(400)
  console.log(req.body.email);
  firebase.auth().signInWithEmailAndPassword(req.body.email, req.body.password).then(function(user){
    authenticated_normal =true;
    firebase.auth().onAuthStateChanged(function(user) {
  if (user) {
    // User is signed in.
    uid = user.uid;
    console.log("Before"+uid);
    res.redirect('current?uid='+uid);
  } else {
    // No user is signed in.
    res.render('normal_login');
  }
});
  }).catch(function(error) {
  res.end("Incorrect Credentials")
   console.log(error.code);
   console.log(error.message);
});
});

app.get('/current',function(req,res){
  if(authenticated_normal==true)
  {
    var user = firebase.auth().currentUser;
    if(user.uid==req.query.uid){
  res.render("current");
  }
  else {
    res.redirect('normal_login');
  }
  }
  else {
    res.redirect('normal_login');
  }
});

app.get('/register',function(req,res){
  res.render("register");
});


app.post('/register',urlencodedParser,function(req,res){
  if (!req.body) return res.sendStatus(400)
  console.log(req.body.email);
  if(req.body.password==req.body.cpassword)
  {
  firebase.auth().createUserWithEmailAndPassword(req.body.email, req.body.password).then(function(user){
    firebase.database().ref('users/'+firebase.auth().currentUser.uid).set({
      email:req.body.email ,
      role:'default',
    });

  }).catch(function(error) {
  var errorCode = error.code;
  var errorMessage = error.message;
  // ...
});
res.redirect('/admin_login');
}
else {
  res.send(500,'Passwords do not match please go back and try again!!!!!!!!!  ');
}
});

app.get('/logout',function(req,res){
  firebase.auth().signOut().then(function() {
  authenticated=false;
  res.redirect('/');
}).catch(function(error) {
  // An error happened.
});
});



app.get('/paytm/generate_checksum',function(request,response){
  var mid;
  var orderid,custid,industryid,channelid,tamount,website,email,mnumber;

  if(request.method == 'GET'){

    console.log(request.query.uid);
  var ref = firebase.database().ref("/Transactions/");
  ref.once("value",function(snapshot){
    snapshot.forEach(function(data){
      if(data.key==request.query.uid)
      {
      console.log("key"+data.key);
      console.log("val"+data.val());
      var paytminfo = data.val();
      console.log("MID"+paytminfo.mid);
      mid=paytminfo.mid;
      orderid=paytminfo.orderId;
      console.log("MID"+orderid);
      custid=paytminfo.customerId;
      console.log("MID"+custid);
      industryid=paytminfo.industryType;
      console.log("MID"+industryid);
      channelid=paytminfo.channel;
      console.log("MID"+channelid);
      tamount=paytminfo.txnAmount;
      console.log("MID"+tamount);
      website=paytminfo.website;
      console.log("MID"+website);
      email=paytminfo.emailId;
      console.log("MID"+email);
      mnumber=paytminfo.mobile;
      console.log("MID"+paytminfo.mid);
      var paramarray = {};
        paramarray['MID'] = mid; //Provided by Paytm
        paramarray['ORDER_ID'] = orderid; //unique OrderId for every request
        paramarray['CUST_ID'] = custid;  // unique customer identifier
        paramarray['INDUSTRY_TYPE_ID'] = industryid; //Provided by Paytm
        paramarray['CHANNEL_ID'] = channelid; //Provided by Paytm
        paramarray['TXN_AMOUNT'] = tamount; // transaction amount
        paramarray['WEBSITE'] = website; //Provided by Paytm
        paramarray['CALLBACK_URL'] = 'https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp';//Provided by Paytm
        paramarray['EMAIL'] = email; // customer email id
        paramarray['MOBILE_NO'] = "9867236820"; // customer 10 digit mobile no.
        console.log(paramarray);
          paytm_checksum.genchecksum(paramarray, paytm_config.MERCHANT_KEY, function (err, res) {
            response.writeHead(200, {'Content-type' : 'text/json','Cache-Control': 'no-cache'});
            response.write(JSON.stringify(res));
            console.log("dddddd"+JSON.stringify(res));
            var ref1 = firebase.database().ref("/Transactions/"+request.query.uid+"/");
            ref1.set({
              mid:mid,
              orderId:orderid,
              customerId:custid,
               mobile:mnumber,
              emailId:email,
              channel:channelid,
              txnAmount:tamount,
              website:website,
              industryType:industryid,
              checksum:res.CHECKSUMHASH
            });

          });
        }
    });
  },function(errorObject){
      response.write("error");
  });

  }else{
    response.writeHead(200, {'Content-type' : 'text/json'});
    console.log("inelse");
    response.end();
  }
});

app.get('/paytm/',function(request,response){
  console.log("/ has started");
  response.writeHead(200 , {'Content-type':'text/html'});
  response.write('<html><head><title>Paytmdddddd</title></head><body>');
  response.write('</body></html>');
  response.end();
});

app.get('/paytm/verify_checksum',function(request,response){
  if(request.method == 'GET'){
    var fullBody = '';
    request.on('data', function(chunk) {
      fullBody += chunk.toString();
    });
    request.on('end', function() {
      var decodedBody = querystring.parse("wb/ma8ue2lwtE1McP6Uj2Uu7tjWo+G1gYdggJSW/bZNPhln710cB+LH5Q0cGds0zO9KtsNZsTBFBXwDgCVHjae/poa5NFEocUTF2CRENXiw=");
      response.writeHead(200, {'Content-type' : 'text/html','Cache-Control': 'no-cache'});
      if(paytm_checksum.verifychecksum(decodedBody, paytm_config.MERCHANT_KEY)) {
        console.log("true");
      }else{
        console.log("false");
      }
       // if checksum is validated Kindly verify the amount and status
       // if transaction is successful
      // kindly call Paytm Transaction Status API and verify the transaction amount and status.
      // If everything is fine then mark that transaction as successful into your DB.

      response.end();
    });
  }else{
    response.writeHead(200, {'Content-type' : 'text/json'});
    response.end();
  }
});

app.get('/generate_checksum',function(req,res){
  res.render("generateChecksum.php");
});


function htmlEscape(str) {
  return String(str)
          .replace(/&/g, '&amp;')
          .replace(/"/g, '&quot;')
          .replace(/'/g, '&#39;')
          .replace(/</g, '&lt;')
          .replace(/>/g, '&gt;');
}

//app.use('/paytm',router);
//module.exports = app;


exports.app = functions.https.onRequest(app);
