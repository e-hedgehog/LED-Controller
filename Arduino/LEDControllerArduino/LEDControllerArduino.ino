#include <SoftwareSerial.h>
#include <string.h>
#include <FastLED.h>

/**
 * define the bluetooth rx and tx pins
 */
//SoftwareSerial BT(10, 11); // RX | TX

/**
 * define constants in code 
 */
#define data 3  //use whichever digital pin you want
#define num_leds 60  //change for your led strip
#define led_type WS2812B   //change to define your led type
#define color_order GRB //change to approriate color order
#define strobeRate 100  //only used if you want a strobe effects

uint8_t gHue = 0;
uint8_t hue = 0;

int curRed;
int curGreen;
int curBlue;

boolean isActive = false; //not currently used

CRGB leds[num_leds];  //define your array of leds

String holdInput = "";
String hold = "";

void setup(){

  pinMode(data, OUTPUT);  //setup data pin

  FastLED.addLeds<led_type, data, color_order>(leds, num_leds).setCorrection(TypicalSMD5050); //define the lights

  /**
   * initially set all of the lights to be turned off
   */
  for(int i = 0; i < num_leds; i++){

    leds[i] = CRGB(0,0,0);
  }
  /**
   * open serial port and bluetooth port
   * change the bluetooth value depending on the baud rate of your bluetooth module
   * I used the HC-06 which has a default of 9600 
   */
  Serial.begin(9600); 
}

/**
 * reads the data from the bluetooth and processes it based on characters defined in the app
 */
void readInputData(){

  if(Serial.available()){

    char inputData = Serial.read();
    holdInput.concat(inputData);

    String identifier = "";

    //Serial.println(holdInput);

    if(inputData == '\n'){
      identifier = holdInput.substring(0, 1);

      if(identifier == "x"){
        readColorData(holdInput);
      }
      if(identifier == "y"){
        readEffectsData(holdInput);
      }
      
      holdInput = "";
    }
  }
}

/**
 * processes the solid light color data 
 * from the app (tabs 1 and 3).
 * sets the strand of lights to one solid color
 * If your power supply does not meet the 
 * current requirements for your led strip the solid
 * color lights may have a gradient effect
 */
void readColorData(String colorData){

  String red = "";
  String green = "";
  String blue= "";

  int R_Index;
  int G_Index;
  int B_Index;
  int end_Index;

  int r_val = 0;
  int g_val = 0;
  int b_val = 0;

  for(int i = 0; i < colorData.length() + 1; i++){
      
    R_Index = colorData.indexOf("R");
    G_Index = colorData.indexOf("G");
    B_Index = colorData.indexOf("B");
    end_Index = colorData.indexOf('\n');
  }
         
  red = colorData.substring(R_Index, G_Index);
  green = colorData.substring(G_Index, B_Index);
  blue = colorData.substring(B_Index, end_Index);
      
  red.remove(0,1);
  blue.remove(0,1);
  green.remove(0,1);

  r_val = red.toInt();
  g_val = green.toInt();
  b_val = blue.toInt();
          
  //Serial.println(r_val);
  //Serial.println(g_val);
  //Serial.println(b_val);

  setColorLight(r_val, g_val, b_val);

  curRed = r_val;
  curGreen = g_val;
  curBlue = b_val;

  colorData = "";
}

/**
 * this function determines which switch has been pressed in the "effects" tab
 * in the app and then begins the function for the desired effect
 */
void readEffectsData(String effectData){
  
  String s = "";
  s = effectData.substring(effectData.length() - 2, effectData.length() - 1);
  
  int switchType;
  switchType = s.toInt();

  //start the function for the desired effect based on switch pressed
  //change the functions in each case to whatever you like this is what I chose, you can write your own functions are below
  switch(switchType){
    case 0:
      setDefault();
      break;
    case 1:
      Serial.println(switchType);
      rainbow();
      break;
    case 2:
      Serial.println(switchType);
      strobe();
      break;
    case 3:
      Serial.println(switchType);
      juggle();
      break;
    case 4:
      Serial.println(switchType);
      changeColor();
      break;
    case 5:
      Serial.println(switchType);
      bpm();
      break;
    case 6:
      Serial.println(switchType);
      pride();
      break;
  }
}

/**
 * this is the default effect
 * everytime an effect is turned off
 * the led strip displays the color it was previously set to
 * before the effect was turned on
 */
void setDefault(){

  fill_solid(leds, num_leds, CRGB(curRed,curGreen,curBlue));
  FastLED.show();
}

/**
 * sets the entire strand of lights to a single solid color
 * WARNING: may create a gradient pattern if the power supply 
 * has an insufficient current supply
 */
void setColorLight(int red, int green, int blue){

  fill_solid(leds, num_leds, CRGB(red, green, blue));
  FastLED.setBrightness(255);
  FastLED.show();   
}

/**
 * this randomly picks a set of 3 lights to turns on and the slowly fade to black
 * each set of lights also is assigned a color with a random hue and gHue which is added to in the loop to constantly 
 * scroll through different colors. 
 */
void changeColor(){

  while(true){

    EVERY_N_MILLISECONDS(20){gHue += 4;}
    fadeToBlackBy(leds, num_leds, 3);
    delay(20);
    int pos = random16(num_leds);
    if(pos == 0){
      leds[pos] += CHSV( gHue + random8(64), 200, 255);
      leds[pos+1] += CHSV( gHue + random8(64), 200, 255);
    }
    else if(pos == 59){
      leds[pos-1] += CHSV( gHue + random8(64), 200, 255);
      leds[pos] += CHSV( gHue + random8(64), 200, 255);
    }
    else{
      leds[pos-1] += CHSV( gHue + random8(64), 255, 255);
      leds[pos] += CHSV( gHue + random8(64), 255, 255);
      leds[pos+1] += CHSV( gHue + random8(64), 255, 255);
    }
    FastLED.show();

    if(Serial.available()){
        
        char inputData = Serial.read();
        
        hold.concat(inputData);

        if(inputData == '\n'){
        
            String s = "";
          
            s = hold.substring(hold.length() - 2, hold.length() - 1);
  
            int switchType;
            switchType = s.toInt();

            Serial.println(switchType);

            if(switchType == 0){

              setDefault();
              break;
      
          hold = "";
          }
        }
      }
  }
}

/**
 * takes the current color of the lights and strobes them at
 * a constant rate which can be changed above with the strobeRate variable
 */
void strobe(){

  while(true){
   
    setColorLight(curRed, curGreen, curBlue);
    FastLED.show();
    delay(strobeRate);
    setColorLight(0, 0, 0);
    FastLED.show();
    delay(strobeRate);

    if(Serial.available()){
        
        char inputData = Serial.read();
        
        hold.concat(inputData);

        if(inputData == '\n'){
        
            String s = "";
          
            s = hold.substring(hold.length() - 2, hold.length() - 1);
  
            int switchType;
            switchType = s.toInt();

            Serial.println(switchType);

            if(switchType == 0){

              setDefault();
              break;
      
          hold = "";
          }
        }
      }
  }
}

/**
 * this function creates a repeating rainbow along the entire strip of lights
 * the hue is updated each 20 ms by a value of 3 which causes the rainbow to constantly
 * shift the color in a cycle
 */
void rainbow(){

  while(true){

    EVERY_N_MILLISECONDS(20){gHue += 3;}
    fill_rainbow(leds, num_leds, gHue, 7);
    FastLED.show();

    if(Serial.available()){
        
    char inputData = Serial.read();
        
    hold.concat(inputData);

    if(inputData == '\n'){
        
      String s = "";
          
      s = hold.substring(hold.length() - 2, hold.length() - 1);
  
      int switchType;
      switchType = s.toInt();

      Serial.println(switchType);

      if(switchType == 0){

        setDefault();
        hold = "";
        break;
      }
    }
  }
  }
}

/**
 * creates 8 different colored dots which randomly travel back and forth
 * along the led strip creating a trail behind them
 */
void juggle() {
  // eight colored dots, weaving in and out of sync with each other

  while(true){
    
    fadeToBlackBy( leds, num_leds, 10);
    byte dothue = 0;
    for( int i = 0; i < 8; i++) {
      leds[beatsin16(i+7,0,num_leds)] |= CHSV(dothue, 200, 255);
      dothue += 32;
    }
    FastLED.show();
    
    if(Serial.available()){
        
    char inputData = Serial.read();
        
    hold.concat(inputData);

    if(inputData == '\n'){
        
      String s = "";
          
      s = hold.substring(hold.length() - 2, hold.length() - 1);
  
      int switchType;
      switchType = s.toInt();

      Serial.println(switchType);

      if(switchType == 0){

        setDefault();
        hold = "";
        break;
      }
    }
  }
  }
}

/**
 * several colored strips pulsing at a specific rate
 * the colors shift by changing the hue of them each 20 ms
 */
void bpm(){

  while(true){
  // colored stripes pulsing at a defined Beats-Per-Minute (BPM)
    EVERY_N_MILLISECONDS(20){gHue += 2;}
    uint8_t BeatsPerMinute = 10;
    CRGBPalette16 palette = PartyColors_p;
    uint8_t beat = beatsin8( BeatsPerMinute, 64, 255);
    for( int i = 0; i < num_leds; i++) {
    
      leds[i] = ColorFromPalette(palette, gHue+(i*2), beat-gHue+(i*10));
    }
    FastLED.show();
    
    if(Serial.available()){
        
    char inputData = Serial.read();
        
    hold.concat(inputData);

    if(inputData == '\n'){
        
      String s = "";
          
      s = hold.substring(hold.length() - 2, hold.length() - 1);
  
      int switchType;
      switchType = s.toInt();

      Serial.println(switchType);

      if(switchType == 0){

        setDefault();
        hold = "";
        break;
      }
    }
  }
  }
}

/**
 * This function draws rainbows with an ever-changing,
 * widely-varying set of parameters.
 */
void pride() 
{
  while (true) {
    static uint16_t sPseudotime = 0;
    static uint16_t sLastMillis = 0;
    static uint16_t sHue16 = 0;
   
    uint8_t sat8 = beatsin88( 87, 220, 250);
    uint8_t brightdepth = beatsin88( 341, 96, 224);
    uint16_t brightnessthetainc16 = beatsin88( 203, (25 * 256), (40 * 256));
    uint8_t msmultiplier = beatsin88(147, 23, 60);
  
    uint16_t hue16 = sHue16;//gHue * 256;
    uint16_t hueinc16 = beatsin88(113, 1, 3000);
    
    uint16_t ms = millis();
    uint16_t deltams = ms - sLastMillis ;
    sLastMillis  = ms;
    sPseudotime += deltams * msmultiplier;
    sHue16 += deltams * beatsin88( 400, 5,9);
    uint16_t brightnesstheta16 = sPseudotime;
    
    for( uint16_t i = 0 ; i < num_leds; i++) {
      hue16 += hueinc16;
      uint8_t hue8 = hue16 / 256;
  
      brightnesstheta16  += brightnessthetainc16;
      uint16_t b16 = sin16( brightnesstheta16  ) + 32768;
  
      uint16_t bri16 = (uint32_t)((uint32_t)b16 * (uint32_t)b16) / 65536;
      uint8_t bri8 = (uint32_t)(((uint32_t)bri16) * brightdepth) / 65536;
      bri8 += (255 - brightdepth);
      
      CRGB newcolor = CHSV( hue8, sat8, bri8);
      
      uint16_t pixelnumber = i;
      pixelnumber = (num_leds-1) - pixelnumber;
      
      nblend( leds[pixelnumber], newcolor, 64);
    }
    FastLED.show();
  
    if(Serial.available()){
          
      char inputData = Serial.read();
          
      hold.concat(inputData);
  
      if(inputData == '\n'){
          
        String s = "";
            
        s = hold.substring(hold.length() - 2, hold.length() - 1);
    
        int switchType;
        switchType = s.toInt();
  
        Serial.println(switchType);
  
        if(switchType == 0){
  
          setDefault();
          hold = "";
          break;
        }
      }
    }
  }
}

void loop(){
  EVERY_N_MILLISECONDS(20){gHue += 5;}
  
  readInputData();
}
