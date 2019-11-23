#include <Servo.h>

Servo myservo;  // create servo object to control a servo
// twelve servo objects can be created on most boards


double angleInput=20; //user input (20 degrees to 70 degrees)

 
int maxValue = 560;   //maximum resistance value from the potentiometer (arm)
int minValue = 201;   //minimum resisitance value from the potentiometer (arm)
double minDegree = 20;  // the degree of the arm when it's not stretched 
double maxDegree = 70;  // the degree of the arm when it's stretched out completely
int curDegree; //current position of the arm in terms of degrees

int pause = 94;  // the value when the servo speed is zero

bool reached = false; //checks if the servo has reached the input position
void setup() {
  Serial.begin(9600);
  myservo.attach(9);  // attaches the servo on pin 9 to the servo object
}

//takes the current position and the position that the arm should move to as input/parameter (must be in terms of degrees)
//The arm moves at a faster speed if it is further away from the position given by the user input, and it moves at a slower speed when it is about to reach the intended location. 
void speedup(int curDegree, int angleInput){
  int difference = abs(curDegree - angleInput);
  if (curDegree > angleInput){ //upward direction
    if (difference > 5){
      myservo.write(110); // faster speed
    }
    else{
      myservo.write(100); // slower speed
    }
  }
  else if (curDegree < angleInput){  //downward direction
    if (difference > 5){
      myservo.write(78); // faster speed 
    }
    else{
      myservo.write(88);  // slower speed
    }
  }
}

void loop() {
  
  if (!reached){
    int val = analogRead(A0);
    curDegree = map(val, minValue, maxValue, minDegree, maxDegree);
    
    Serial.println(curDegree);
    if (curDegree >= maxDegree){
        //pause 
        myservo.write(pause);
    }
    else if (curDegree <= minDegree){
        myservo.write(pause);
    }
    else if (abs(curDegree - angleInput) <= 3){
        //pause after reaching the intended location
        reached = true;
        myservo.write(pause);
       
    }
    else{
        speedup(curDegree, angleInput);
    }
  }
  
  delay(15);                       // waits 15ms for the servo to reach the position

  // minimum degree is 20 
  // maximum degree is 70
  
  //94 ia speed 0. Greater than 94 results in degree increase; direction of 90 degrees.
  //560 being default position. 201 being 90 degrees.
}
