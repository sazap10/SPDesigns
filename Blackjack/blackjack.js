window.onload = function(){
	var canvas = document.getElementById("canvas");
	var context = canvas.getContext("2d");
	var width = canvas.width;
	var height = canvas.height;
	context.fillStyle= "white";
	context.font = "16pt Calibri";
	context.fillText("Your Hand",5,15);
	context.fillText("Dealer Hand",5,165);
	$hit.attr("disabled", true);
	$stick.attr("disabled", true);
	playGame(context);
}
$deal = $('.deal');
$hit = $('.hit');
$stick= $('.stick');
var state = 0;
CanvasRenderingContext2D.prototype.clear = function (preserveTransform) {
  if (preserveTransform) {
    this.save();
    this.setTransform(1, 0, 0, 1, 0, 0);
  }

  this.clearRect(0, 0, this.canvas.width, this.canvas.height);

  if (preserveTransform) {
    this.restore();
  }         
};
/*
*  
* Start of drawing methods
*
* roundRect(x,y,width,height,radius,fill,stroke) 
* @param {Number} x The top left x coordinate
* @param {Number} y The top left y coordinate 
* @param {Number} width The width of the rectangle 
* @param {Number} height The height of the rectangle
* @param {Number} radius The corner radius. Defaults to 5;
* @param {Boolean} fill Whether to fill the rectangle. Defaults to false.
* @param {Boolean} stroke Whether to stroke the rectangle. Defaults to true.
*/
CanvasRenderingContext2D.prototype.roundRect = function(x, y, width, height, radius, fill, stroke) {
	if (typeof stroke == "undefined" ) {
			stroke = true;
		  }
		  if (typeof radius === "undefined") {
			radius = 5;
		  }
		  this.beginPath();
		  this.moveTo(x + radius, y);
		  this.lineTo(x + width - radius, y);
		  this.quadraticCurveTo(x + width, y, x + width, y + radius);
		  this.lineTo(x + width, y + height - radius);
		  this.quadraticCurveTo(x + width, y + height, x + width - radius, y + height);
		  this.lineTo(x + radius, y + height);
		  this.quadraticCurveTo(x, y + height, x, y + height - radius);
		  this.lineTo(x, y + radius);
		  this.quadraticCurveTo(x, y, x + radius, y);
		  this.closePath();
		  if (stroke) {
			this.stroke();
		  }
		  if (fill) {
			this.fill();
		  }        
	}

/*
*  
* End of roundRect method
*
* createCard(x,y,num,suit,width,height,colour) 
* @param {Number} x The top left x coordinate
* @param {Number} y The top left y coordinate 
* @param {String} num The number of the card
* @param {String} suit The suit of the card (Represented by symbols)
* @param {Number} width The width of the card 
* @param {Number} height The height of the card
* @param {String} colour The colour of the text
*/
function createCard(context,x,y,num,suit,width,height,colour){
	context.save();
	context.save()
	context.lineWidth   = 2;
	context.fillStyle = "white";
	context.strokeStyle = "#000";
	context.shadowOffsetX = 5;
	context.shadowOffsetY = 5;
	context.shadowBlur    = 4;
	context.shadowColor   = 'rgba(0, 0, 0, 0.5)';
	context.roundRect(x,y,width,height,5,true,true);
	context.restore();
	context.fillStyle = colour;
	context.font = "10pt Calibri";
	context.textAlign = "center";
	context.fillText(num, x+8, y+15);
	context.fillText(suit,x+8, y+25);
	context.translate(x+width-5, y+height-15);
	context.rotate(-Math.PI);
	context.fillText(num, 3, 0);
	context.fillText(suit,3, 10);
	context.restore();
}
/*
*  
* End of createCard method
*
* Set up the instance variables
* colour - list of the colours of card
* cardNum - list of numbers on the card
* suitName - list of symbols of each suit
* cards - list used to store the cards in the deck
*/
var colour= ["black","red"];
var cardNum = ["A",2,3,4,5,6,7,8,9,10,"J","Q","K"];
var suitName = [eval('"\\u2660"'),eval('"\\u2665"'),eval('"\\u2663"'),eval('"\\u2666"')];
var cards=[];
/* 
* createDeck()
* creates the deck by creating a card and storing them to cards
* shuffles the deck using the shuffleDeck method
*/
function createDeck(){
    for(var i = 1;i<=13;i++){
        for(var j = 1;j<=4;j++){
            cards.push(new Card(j,i));
        }
    }
    cards = shuffleDeck(cards);
}
/* 
* shuffleDeck()
* shuffles the deck to have the cards stored randomly
*/
function shuffleDeck(v){
    for(var j, x, i = v.length; i; j = parseInt(Math.random() * i), x = v[--i], v[i] = v[j], v[j] = x);
    return v;
}
/*
* Prototype for the Card type
*/
function Card(sui,num){
    var suit = sui;
    var number = num;
	//returns the card number
    this.getNumber = function(){
        return number;
    };
	//returns the suit number
    this.getSuit = function(){
        return suit;
    };
	//return card's value
    this.getValue = function(){
        if(number === 1)
            return 11;
        else if(number >= 11 && number <= 13)
            return 10;
        else 
            return number;
    };
}
/*
* Prototype of Hand type
*/
function Hand(context,x,y){
    var cards =[];
    var handx=x;
    var handy=y;
	//set up initial hand with two cards
    cards.push(deal());
    createCard(context,x,y,cardNum[cards[0].getNumber()-1],suitName[cards[0].getSuit()-1],60,100,colour[(cards[0].getSuit()-1)%2]);
    x+=75;
    cards.push(deal());
    createCard(context,x,y,cardNum[cards[1].getNumber()-1],suitName[cards[1].getSuit()-1],60,100,colour[(cards[1].getSuit()-1)%2]);
    x+=75;
	//returns the cards in the hand
    this.getHand = function(){
        return cards;
    };
	//returns the score of the hand
    this.score = function(){
        var result=0;
        var numOfAces =0;
        for(var i = 0;i<cards.length;i++){
            var card =cards[i].getValue();
            if(card === 11)
                numOfAces++;
            result+=card;
            if(result>21 && numOfAces >0){
                result-=10;
                numOfAces--;
            }
        }
        return result;
    };
	//prints the hand to console
    this.printHand = function(){
        var result = "";
        for(var i = 0;i< cards.length;i++){
            var num = cards[i].getNumber();
            var suit = cards[i].getSuit();
            result+=cardNum[num-1]+" of "+suitName[suit-1];
            if(i!= cards.length-1)  
                result+=", ";
        }
        return result;
    };
	//adds new card to deck
    this.hitMe = function(){
        cards.push(deal());
        createCard(context,x,y,cardNum[cards[cards.length-1].getNumber()-1],suitName[cards[cards.length-1].getSuit()-1],60,100,colour[(cards[cards.length-1].getSuit()-1)%2]);
    	x+=75;
    };
}
//pops a card from the cards array
function deal(){
    return cards.pop();
}
//plays as the computer
function playAsDealer(context){
    var hand = new Hand(context,5,180);
    var score = hand.score();
    while(score < 17){
        hand.hitMe();
        score = hand.score();
		context.fillText("Dealer Score: "+hand.score(),5,330);
    }
    return hand;
}
//play as the user 
function playAsUser(context){
    var hand = new Hand(context,5,30);
    var hit = true;
    while(hit){
        var score = hand.score();
        hit = confirm("Total score: "+score+"\n"+"Hit me?");
        if(score>21) break;
        if(hit) hand.hitMe();
		context.clearRect(5,310,200,20);
		context.fillText("Your Score: "+hand.score(),5,310);
    }
    return hand;
}

function declareWinner(userHand,dealerHand){
    userScore = userHand.score();
    dealerScore = dealerHand.score();
    if(userScore>21 && dealerScore >21)
        return "You tied!";
    else if(userScore > 21)
        return "You lose!";
    else if(dealerScore > 21)
        return "You win!";
    else{
        if(userScore > dealerScore)
            return "You win!";
        else if(userScore === dealerScore)
            return "You tied!";
        else
            return "You lose!";
    }
}

function playGame(context){
    createDeck();
    var user = playAsUser(context);
    var dealer = playAsDealer(context);
    var winner = declareWinner(user,dealer);
    console.log(user.printHand()+"\n"+"Total score: "+user.score());
    context.fillText("Your Score: "+user.score(),5,310);
    console.log(dealer.printHand()+"\n"+"Total score: "+dealer.score());
    context.fillText("Dealer Score: "+dealer.score(),5,330);
    console.log(winner);
    context.fillText(winner,5,350);
}

