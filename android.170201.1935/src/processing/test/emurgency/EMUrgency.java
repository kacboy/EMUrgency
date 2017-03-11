package processing.test.emurgency;

import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import android.os.Environment; 
import apwidgets.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class EMUrgency extends PApplet {




//sound
//APMediaPlayer audio;
int music;
int sfx;

//saving
String directory = new String(Environment.getExternalStorageDirectory().getAbsolutePath()) + "/EMUrgency";
String myfn="EMUrgency.txt";    

PFont nokia;
PImage earth, moon, logo, airSmall, airBig, obstacle1, obstacleBig, meteor1, meteor2, meteor3, meteor4, top, top2, front, front2, front3, front4, packtop, credits, help, sound1, sound2;
int logoC = color(random(0, 255), random(0, 200), random(0, 200));

//background
ArrayList<Stars> stars; 
float earthX, earthY;

//timers
int frames=0, seconds=0, minutes=0;

//data
int minsHS, secsHS;

//gameStates
int gameState=0;

//stats
float obstacleMax=5;
float airMax=2;
int airOne=10;
int airTwo=25;
float dodged=0;
int spawnPack=0;

//gameplay objects
ArrayList<Obstacle> obstacle; 
ArrayList<Obstacle2> obstacle2; 
ArrayList<Air> air;
ArrayList<Air2> air2;
ArrayList<Particles> particles; 
Player player; 
Pack pack;

public void setup() {
  orientation(PORTRAIT); 
   
  //size(displayWidth, displayHeight);
  //size(480, 853);
  //size(540, 960);
  //size(480, 720);

  //sound
  //audio = new APMediaPlayer(this); //create new APMediaPlayer 
  //audio.setMediaFile("aquanaut.mp4"); //set the file (files are in data folder) 
  //audio.start(); //start play back 
  //audio.setLooping(true); //restart playback end reached
  //audio.setVolume(1.0, 1.0); //Set left and right volumes. Range is from 0.0 to 1.0

  //font
  //nokia = loadFont("nokia.vlw");
  nokia = createFont("nokiafc22.ttf", 50, false);
  textFont(nokia);
  textSize(width/25);

  //object initiating
  stars = new ArrayList<Stars>();
  obstacle = new ArrayList<Obstacle>();
  obstacle2 = new ArrayList<Obstacle2>();
  air = new ArrayList<Air>();
  air2 = new ArrayList<Air2>();
  particles = new ArrayList<Particles>();
  player = new Player();
  pack = new Pack(new PVector(-100, -100), new PVector(0, 0));

  //images
  logo = loadImage("logo.png");
  credits = loadImage("credits.png");
  help = loadImage("help.png");
  top = loadImage("eva-packtop.png");
  top2 = loadImage("eva-packtop2.png");
  front = loadImage("eva-packfront.png");
  front2 = loadImage("eva-packhands.png");
  front3 = loadImage("eva-front.png");
  front4 = loadImage("eva-hands.png");
  packtop = loadImage("packtop.png");
  airSmall = loadImage("airSmall.png");
  airBig = loadImage("airBig.png");
  obstacle1 = loadImage("obstacle1.png");
  obstacleBig = loadImage("obstacle2.png");
  meteor1 = loadImage("meteor1.png");
  meteor2 = loadImage("meteor2.png");
  meteor3 = loadImage("meteor3.png");
  meteor4 = loadImage("meteor4.png");
  sound1 = loadImage("sound1.png");
  sound2 = loadImage("sound2.png");
  logo.resize(width/3*2, 0);
  credits.resize(width/3*2, 0);
  help.resize(width/12*11, 0);
  sound1.resize(width/8, 0);
  sound2.resize(width/8, 0);
  front.resize((int)player.cWidth, 0);
  front2.resize((int)player.cWidth, 0);
  front3.resize((int)player.cWidth, 0);
  front4.resize((int)player.cWidth, 0);
  top.resize(0, (int)player.cHeight);
  top2.resize(0, (int)(player.cHeight*1.45f));
  packtop.resize((int)player.cWidth, 0);
  airSmall.resize(0, (int)(width/20));
  airBig.resize(0, (int)(width/11));
  obstacle1.resize(width/10, 0);
  obstacleBig.resize(width/3, 0);
  meteor1.resize(width/10, 0);
  meteor2.resize(width/10, 0);
  meteor3.resize(width/3, 0);
  meteor4.resize(width/3, 0);

  loadData();

  //background
  earth=loadImage("earth.png");
  moon=loadImage("moon.png");
  earth.resize((int)random(width/2, height/4*3), 0);
  moon.resize((int)random(width/12, width/6), 0);
  earthX=random(0-width/2, width*1.5f);
  earthY=random(height/2, height*1.5f);
  for (int s=0; s<150; s++) {
    PVector spawnVector = new PVector(random(0, width), random(0, height));
    stars.add(new Stars(spawnVector));
  }
}

public void draw() { 
  //sound
  if (music==1) {
    //audio.start(); //start play back 
    //audio.setLooping(true); //restart playback end reached
  }

  //background
  background(0);
  for (int s=0; s<stars.size(); s++) {
    stars.get(s).drawMe();
  }
  imageMode(CENTER);
  image(moon, earthY-width*1.25f, earthX+width/2);
  image(earth, earthX, earthY);
  fill(0, map(player.health, player.maxHealth, 0, 0, 255));
  rectMode(CORNER);
  if (gameState!=-1) rect(-5, -5, width+10, height+10);

  //gameplay objects
  obstacleSpawn();
  airSpawn();

  //player drawing
  player.update();
  player.drawMe();
  particlesSpawn();

  HUD();

  //gameState changes
  if (player.health==-1) gameState=-1;

  println("music:"+music+" sfx:"+sfx+"g ameState:"+gameState+" obstacles:"+obstacle.size(), "dodged:"+dodged, "obstacleMax:"+obstacleMax, "obstacleMax/2:"+obstacleMax/2);
}

public void obstacleSpawn() {
  //obstacle spawn
  if ((obstacle.size()+obstacle2.size())<obstacleMax&&(gameState==0||gameState==1)) {
    PVector spawnVector = new PVector(random(0, width), random(-width/3, -(width/3)/2));
    if (random(0, 500)<obstacleMax/4&&obstacleMax/4<10) {
      if (random(-1, 1)<0) {
        obstacle.add(new Obstacle(spawnVector));
      } else {
        obstacle2.add(new Obstacle2(spawnVector));
      }
    } else {
      if (random(0, 500)<10) {
        if (random(-1, 1)<0) {
          obstacle.add(new Obstacle(spawnVector));
        } else {
          obstacle2.add(new Obstacle2(spawnVector));
        }
      }
    }
  }
  //obstacle drawing+hit
  for (int o=0; o<obstacle.size(); o++) {
    obstacle.get(o).update();
    obstacle.get(o).drawMe();
    //player hit
    if (player.detectHit(obstacle.get(o)) && player.invincibleTimer==0 && (player.location.x!=width/2&&player.location.y!=height/4*3)) {
      player.hit();
      player.velocity.x=player.velocity.x*-1;
      player.velocity.y=player.velocity.y*-1;
    }
    if (obstacle.get(o).pos.y-obstacle.get(o).cHeight/2>height||obstacle.get(o).pos.x-obstacle.get(o).cWidth/2>width||obstacle.get(o).pos.x+obstacle.get(o).cWidth/2<0) { 
      obstacle.remove(o);
      dodged++;
      obstacleMax+=dodged*.0025f;
    }
  }
  //obstacle2 drawing+hit
  for (int o=0; o<obstacle2.size(); o++) {
    obstacle2.get(o).update();
    obstacle2.get(o).drawMe();
    //player hit
    if (player.detectHit(obstacle2.get(o)) && player.invincibleTimer==0 && (player.location.x!=width/2&&player.location.y!=height/4*3)) {
      player.hit();
      player.velocity.x=player.velocity.x*-1;
      player.velocity.y=player.velocity.y*-1;
    }
    if (obstacle2.get(o).pos.y-obstacle2.get(o).cHeight/2>height||obstacle2.get(o).pos.x-obstacle2.get(o).cWidth/2>width||obstacle2.get(o).pos.x+obstacle2.get(o).cWidth/2<0) { 
      obstacle2.remove(o);
      dodged++;
      obstacleMax+=dodged*.0025f;
    }
  }
}

public void airSpawn() {
  //air spawn
  if ((air.size()+air2.size())<airMax&&(gameState==0||gameState==1)) {
    PVector spawnVector = new PVector(random(0, width), random(-width/11, -(width/11)/2));
    if (random(0, 500)<5) 
      if (random(-1, 1)<0) {
        air.add(new Air(spawnVector));
      } else {
        air2.add(new Air2(spawnVector));
      }
  }
  //air drawing+hit
  for (int a=0; a<air.size(); a++) {
    air.get(a).update();
    air.get(a).drawMe();
    //player hit
    if (player.detectHit(air.get(a)) && player.health!=-1 && (player.location.x!=width/2&&player.location.y!=height/4*3)) {
      if (player.health<=player.maxHealth-airTwo) player.health+=airOne; 
      else { 
        player.health=player.maxHealth;
      }
      air.remove(a);
    } else if (air.get(a).pos.y-air.get(a).cHeight/2>height) {
      air.remove(a);
    }
  }
  //air2 drawing+hit
  for (int a=0; a<air2.size(); a++) {
    air2.get(a).update();
    air2.get(a).drawMe();
    //player hit
    if (player.detectHit(air2.get(a)) && player.health!=-1 && (player.location.x!=width/2&&player.location.y!=height/4*3)) {
      if (player.health<=player.maxHealth-airTwo) player.health+=airTwo;
      else { 
        player.health+=airTwo;
        player.maxHealth+=airOne;
      }
      air2.remove(a);
    } else if (air2.get(a).pos.y-air2.get(a).cHeight/2>height) {
      air2.remove(a);
    }
  }
}

public void particlesSpawn() {
  //particle drawing
  if (clicked && player.health>0 && millis()%2==0 && player.invincibleTimer==0 && gameState!=0) {
    particles.add(new Particles(player.location.get(), player.velocity.get()));
  }
  for (int p=0; p<particles.size(); p++) {
    particles.get(p).update();
    particles.get(p).drawMe();

    if (particles.get(p).timer>25) particles.remove(p);
  }
}

public void HUD() {
  if (gameState==-1) {
    //spawn pack
    if (spawnPack==0) {
      pack.vel.x=-player.velocity.x;
      pack.vel.y=-player.velocity.y;
      pack.pos.x=player.location.x;
      pack.pos.y=player.location.y;
      spawnPack=1;
    }
    pack.drawMe();
    pack.update();

    //gameover
    fill(255, 0, 0);
    textSize(width/(25/2));
    text("GAME OVER", width/2, height/3);
    textSize(width/25);
    saveData();

    //reset button
    noStroke();
    rectMode(CORNER);
    if (Button("RESET", width/2-width/8, height-width/5, width/4, width/8)&&player.health<=0) {
      gameState=0;
      spawnPack=0;
      setup();
    }
    text("AIR:00.0 | TIME:"+minutes+":"+nf(seconds, 2)+" | HS:"+minsHS+":"+nf(secsHS, 2), width/2, width/12);
  } else if (gameState==0) {
    //start menux
    logo();
    fill(255);
    text("TAP TO START", width/2, height/2);
    rectMode(CORNER);
    if (Button("HELP", width/5-width/8, height-width/5, width/4, width/8)) {
      gameState=2;
      player.location.x=-100;
      player.location.y=-100;
      player.health=player.maxHealth;
    } else if (Button("SETTINGS", width/2-width/8, height-width/5, width/4, width/8)) {
      gameState=3;
      player.location.x=-100;
      player.location.y=-100;
      player.health=player.maxHealth;
    } else if (Button("CREDITS", width/5*4-width/8, height-width/5, width/4, width/8)) {
      gameState=4;
      player.location.x=-100;
      player.location.y=-100;
      player.health=player.maxHealth;
    }
  } else if (gameState==1) {
    //playing
    timers();
  } else if (gameState==2) {
    helpMenu();
    player.location.x=-100;
    player.location.y=-100;
    earthX=-1000;
    earthY=-1000;
    player.health=player.maxHealth;
    rectMode(CORNER);
    if (Button("BACK", width/2-width/8, height-width/5, width/4, width/8)) {
      gameState=0;
      setup();
    }
  } else if (gameState==3) {
    settingsMenu();
    player.location.x=-100;
    player.location.y=-100;
    earthX=-1000;
    earthY=-1000;
    player.health=player.maxHealth;
    rectMode(CORNER);
    if (Button("BACK", width/2-width/8, height-width/5, width/4, width/8)) {
      gameState=0;
      setup();
    }
  } else if (gameState==4) {
    credits();
    player.location.x=-100;
    player.location.y=-100;
    earthX=-1000;
    earthY=-1000;
    player.health=player.maxHealth;
    rectMode(CORNER);
    if (Button("BACK", width/2-width/8, height-width/5, width/4, width/8)) {
      gameState=0;
      setup();
    }
  }
  if (gameState!=-1&&gameState<2) {
    fill(255);
    if (player.health>=10) text("AIR:" +player.health+" | TIME:"+minutes+":"+nf(seconds, 2)+" | HS:"+minsHS+":"+nf(secsHS, 2), width/2, width/12);
    else if (player.health<10&&player.health>0) text("AIR:0" +player.health+" | TIME:"+minutes+":"+nf(seconds, 2)+" | HS:"+minsHS+":"+nf(secsHS, 2), width/2, width/12);
    else {
      text("AIR:00.0 | TIME:"+minutes+":"+nf(seconds, 2)+" | HS:"+minsHS+":"+nf(secsHS, 2), width/2, width/12);
    }
  }
}

public void timers() {
  frames++;
  if (frames>=frameRate) {
    frames=0;
    seconds++;
  }
  if (seconds>=60) {
    seconds=0;
    minutes++;
  }
}

public void helpMenu() {
  fill(255);
  //text("HELP", width/2, width/12);
  text("AIR:100.0 | TIME:0:00 | HS:"+minsHS+":"+nf(secsHS, 2), width/2, width/12);
  imageMode(CENTER);
  image(help, width/2, height/20*9);
}

public void settingsMenu() {
  logo();
  fill(255);
  text("SETTINGS", width/2, width/12);
  rectMode(CORNER);
  imageMode(CORNER);
  if (Button("MUSIC", width/4, height/2-width/5, width/4, width/8)) {
    if (music==1) {
      music=0;
      //audio.pause();
    } else music=1;
    minutes=-1;
    saveData();
  }
  if (Button("SOUNDS", width/4, height/2, width/4, width/8)) {
    if (sfx==1) sfx=0;
    else sfx=1;
    minutes=-1;
    saveData();
  }
  if (music==1) {
    image(sound2, width/2+width/12, height/2-width/5);
  } else {
    image(sound1, width/2+width/12, height/2-width/5);
  }
  if (sfx==1) {
    image(sound2, width/2+width/12, height/2);
  } else {
    image(sound1, width/2+width/12, height/2);
  }
  if (Button("RESET HS", width/2-width/8, height/2+width/5, width/4, width/8)) {
    saveData();
  }
  imageMode(CENTER);
}

public void credits() {
  logo();
  fill(255);
  textSize(width/25);
  text("CREDITS", width/2, width/12);
  text("NO SOUND v1.0", width/2, height/2-width/10);
  //text("VERSION 0."+((year()-2016)+(30*month()-8)+day()), width/2, height/2-width/10);
  text("EXTRAVEHICULAR MOBILITY UNIT", width/2, height/2-width/3); 
  textSize(width/30);
  text("ROUTINE GONE EXTREMELY NOT COOL YO", width/2, height/2-width/3+width/15);
  imageMode(CORNER);
  image(credits, width/2-credits.width/2, height/2);
  textSize(width/25);
}

public void logo() {
  fill(logoC);
  rectMode(CENTER);
  image(logo, width/2, height/4);
  //rect(width/2, height/4*.98, width/3*1.98, height/17);
  //rect(width/4*2.05, height/4*1.08, width/22, height/35);
  //rect(width/4*3.165, height/4*1.08, width/22, height/35);
}

public void loadData() {
  //String[] data = loadStringsFromResourceNamed("data.txt"); 
  //String data[] = loadStrings("data.txt");
  //minsHS=int(data[0]);
  //secsHS=int(data[1]);
  String lines[];
  File afile = new File(directory+"/"+myfn);
  if (afile.exists()) {
    lines = loadStrings(afile.getAbsoluteFile());
    println(lines.length, lines);
    if (lines.length==1) {
      lines = split(lines[0], '$');
    } 
    minsHS=PApplet.parseInt(lines[1]);
    secsHS=PApplet.parseInt(lines[2]);
    music=PApplet.parseInt(lines[3]);
    sfx=PApplet.parseInt(lines[4]);
  } else {
    lines=null;
  }
}

public void saveData() {
  if (minutes>=minsHS&&seconds>secsHS||minutes>minsHS||minutes==0&&seconds==0) {
    String save = "$"+str(minutes)+"$"+str(seconds)+"$"+str(music)+"$"+str(sfx);
    //String[] saved = split(save, '$');
    //saveStrings("data.txt", saved);    
    PrintWriter  output = createWriter(directory + "/" + "EMUrgency.txt"); 
    output.println(save);
    output.flush();
    output.close();
  } else if (minutes<0) {
    String save = "$"+str(minsHS)+"$"+str(secsHS)+"$"+str(music)+"$"+str(sfx);
    //String[] saved = split(save, '$');
    //saveStrings("data.txt", saved);    
    PrintWriter  output = createWriter(directory + "/" + "EMUrgency.txt"); 
    output.println(save);
    output.flush();
    output.close();
  }
}

//The MediaPlayer must be released when the app closes public void onDestroy()
//public void onDestroy() {
//  super.onDestroy();
//  if (audio != null) {
//    audio.release();
//  }
//}

class Air extends Obstacle {
  int spin= (int)random(0, 180);
  float direction= random(-1, 1);
  float FPS=random(60/frameRate, 120/frameRate);

  //CONSTRUCTOR
  Air(PVector pos) {
    super(pos);
    this.pos = pos;
    cWidth=width/20;
    cHeight=cWidth;
    vel = new PVector(random(-width/240, width/240), random(width/240, width/60));
  }

  public void update() {
    super.update();

    //screenbox
    if (gameState==0||gameState==1) {
      if (pos.x+cWidth/2<0) pos.x=width+(cWidth/2);
      else if (pos.x-cWidth/2>width) pos.x=0-(cWidth/2);
    } else {
      if (pos.x+cWidth/2<0||pos.x-cWidth/2>width) pos.y=height+cHeight;
    }
    //if (pos.y+cHeight/2<0) pos.y=height+(cHeight/2);
    //else if (pos.y-cHeight/2>height) pos.y=0-(cHeight/2);

    if (direction<0) {
      spin-=FPS;
    } else if (direction>=0&&direction<1) {
    } else {
      spin+=FPS;
    }
  }

  public void drawMe() {
    pushMatrix();
    translate(pos.x, pos.y);
    rotate(radians(spin));
    imageMode(CENTER);
    image(airSmall, 0, 0);
    popMatrix();
  }
}
class Air2 extends Obstacle {
  int spin= (int)random(0, 180);
  float direction= random(-1, 1);
  float FPS=random(60/frameRate, 180/frameRate);

  //CONSTRUCTOR
  Air2(PVector pos) {
    super(pos);
    this.pos = pos;
    cWidth=width/11;
    cHeight=cWidth;
    vel = new PVector(random(-width/240, width/240), random(width/240, width/60));
  }

  public void update() {
    super.update();

    //screenbox
    if (gameState==0||gameState==1) {
      if (pos.x+cWidth/2<0) pos.x=width+(cWidth/2);
      else if (pos.x-cWidth/2>width) pos.x=0-(cWidth/2);
    } else {
      if (pos.x+cWidth/2<0||pos.x-cWidth/2>width) pos.y=height+cHeight;
    }
    //if (pos.y+cHeight/2<0) pos.y=height+(cHeight/2);
    //else if (pos.y-cHeight/2>height) pos.y=0-(cHeight/2);

    if (direction<0) {
      spin-=FPS;
    } else if (direction>=0&&direction<1) {
    } else {
      spin+=FPS;
    }
  }

  public void drawMe() {
    pushMatrix();
    translate(pos.x, pos.y);
    rotate(radians(spin));
    imageMode(CENTER);
    image(airBig, 0, 0);
    popMatrix();
  }
}
/*---------------------------------------
 UI Components of Cards_UI for Processing
 author: Lucas Cassiano - cassiano@mit.edu
 date: July 3rd, 2016
 lastUpdate: July 17th, 2016
 version: 1.03
 
 Controllers:
 -Buttonent
 -ImageButton Basic
 -Tooltip
 -Slider
 -Toggle
 -Card
 -Movable Cards
 */
//Colors
private int c_very_dark = color(36, 37, 46);
private int c_dark = color(29, 33, 44);
private int c_mid = color(44, 58, 71);
private int c_light= color(255/4);

private int c_primary= color(33, 115, 139);
private int c_hover = color(32, 155, 160);

private int c_text_color = color(255);
private int top_right = color(255);

//Click Options
private boolean clicked = false;
private boolean canClick = true;



//For text Input/Edit
String bufferText = null;
boolean doneText = false;

//Default sizes
private int s_big = 200;
private int s_height = 30;
private int s_med = 100;
private int s_small = 50;

//For Cards
int card_h = 0;
int card_w = 0;
private int card_x = 0;
private int card_y = 0;

public void uiDark() {
  c_very_dark = color(36, 37, 46);
  c_dark = color(29, 33, 44);
  c_mid = color(44, 58, 71);
  c_light = color(51, 64, 80);
  c_hover = color(32, 155, 160);
}

public void uiLight() {
  c_very_dark = color(100);
  c_dark = color(150);
  c_mid = color(200);
  c_light = color(250);
  c_hover = color(32, 155, 160);
  c_text_color = color(10);
}

//Basic Text Button
public boolean Button(String text, int x, int y, int w, int h) {

  if (mouseX >= x && mouseX <= x+w && 
    mouseY >= y && mouseY <= y+h) {
    fill(c_light);
    rect(x, y, w, h);
    fill(c_text_color);
    textAlign(CENTER, CENTER);
    text(text, x, y, w, h);
    if (clicked && canClick) {
      fill(c_light);
      rect(x, y, w, h);
      text(text, x, y, w, h);
      canClick = false;
      return true;
    }
  } else {
    fill(c_light);
    rect(x, y, w, h);
    fill(c_text_color);

    textAlign(CENTER, CENTER);
    text(text, x, y, w, h);
    return false;
  }

  return false;
}

//Basic Text Button
public boolean Button(String text, int x, int y) {
  return Button(text, x, y, s_med, s_height);
}

//Basic Text Button
public boolean Button(String text, int x, int y, String t) {
  return Button(text, x, y, s_med, s_height, t);
}

//X and Y are the position of the point of the triangle
public void Tooltip(String text, int x, int y) {
  int w = (int)textWidth(text);
  int h = 50;
  int tw = 14; //triangle width
  int th = 15; //triangle height
  noStroke();
  //Shadow
  fill(0, 0, 0, 15);
  rect(5+x-w/2, 5+y-th-h, w, h, 2);
  triangle(5+x-tw/2, 5+y-th, 5+x, 5+y, 5+x+tw/2, 5+y-th);
  //Color
  fill(c_very_dark);
  rect(x-w/2, y-th-h, w, h, 2);
  triangle(x-tw/2, y-th, x, y, x+tw/2, y-th);
  //Text
  fill(255);
  textAlign(CENTER, CENTER);
  text(text, x-w/2, y-th-h, w, h);
  //triangle(
}

//Button With Tooltip
public boolean Button(String text, int x, int y, int w, int h, String tooltip) {
  if (mouseX >= x && mouseX <= x+w && 
    mouseY >= y && mouseY <= y+h) {
    Tooltip(tooltip, x+w/2, y-1);
    fill(c_hover);
    rect(x, y, w, h);
    fill(c_text_color);
    textAlign(CENTER, CENTER);
    text(text, x, y, w, h);
    if (clicked && canClick) {
      fill(c_light);
      rect(x, y, w, h);
      text(text, x, y, w, h);
      canClick = false;
      return true;
    }
  } else {
    fill(c_light);
    rect(x, y, w, h);
    fill(c_text_color);
    textAlign(CENTER, CENTER);
    text(text, x, y, w, h);
    return false;
  }

  return false;
}
//Basic Image Button
public boolean ImageButton(PImage img, int x, int y, int w, int h) {
  if (mouseX >= x && mouseX <= x+w && 
    mouseY >= y && mouseY <= y+h) {
    fill(c_dark);
    rect(x, y, w, h);
    image(img, x, y, w, h);
    if (clicked && canClick) {
      fill(c_mid);
      rect(x, y, w, h);
      image(img, x, y, w, h);
      canClick = false;
      return true;
    }
  } else {
    fill(c_mid);
    rect(x, y, w, h);
    image(img, x, y, w, h);
    return false;
  }

  return false;
}

//Basic ImageButton with padding
public boolean ImageButton(PImage img, int x, int y, int w, int h, int padding) {
  if (mouseX >= x && mouseX <= x+w && 
    mouseY >= y && mouseY <= y+h) {
    fill(c_dark);
    rect(x, y, w, h);
    image(img, x+padding, y+padding, w-2*padding, h-2*padding);
    if (clicked && canClick) {
      fill(c_mid);
      rect(x, y, w, h);
      image(img, x, y, w, h);
      canClick = false;
      return true;
    }
  } else {
    fill(c_mid);
    rect(x, y, w, h);
    image(img, x+padding, y+padding, w-2*padding, h-2*padding);
    return false;
  }

  return false;
}

//Image Button with selected state
public boolean ImageButton(PImage img, int x, int y, int w, int h, boolean select) {
  if (select) {
    fill(c_dark);
    rect(x, y, w, h);
    image(img, x, y, w, h);
    //return true;
  } else if (mouseX >= x && mouseX <= x+w && 
    mouseY >= y && mouseY <= y+h) {
    fill(c_dark);
    rect(x, y, w, h);
    image(img, x, y, w, h);
    if (clicked && canClick) {
      fill(c_mid);
      rect(x, y, w, h);
      image(img, x, y, w, h);
      canClick = false;
      return true;
    }
  } else {
    fill(c_mid);
    rect(x, y, w, h);
    image(img, x, y, w, h);
    return false;
  }

  return false;
}



//ImageButton with selected state and with padding
public boolean ImageButton(PImage img, int x, int y, int w, int h, boolean select, int padding) {
  if (select) {
    fill(c_dark);
    rect(x, y, w, h);
    image(img, x+padding, y+padding, w-2*padding, h-2*padding);
    //return true;
  } else if (mouseX >= x && mouseX <= x+w && 
    mouseY >= y && mouseY <= y+h) {
    fill(c_dark);
    rect(x, y, w, h);
    image(img, x+padding, y+padding, w-2*padding, h-2*padding);
    if (clicked && canClick) {
      fill(c_mid);
      rect(x, y, w, h);
      image(img, x+padding, y+padding, w-2*padding, h-2*padding);
      canClick = false;
      return true;
    }
  } else {
    fill(c_mid);
    rect(x, y, w, h);
    image(img, x, y, w, h);
    return false;
  }
  return false;
}


public void mousePressed() {
  clicked = true;
}

public void mouseReleased() {
  clicked = false;
  canClick  = true;
}

//void keyPressed() {
//  if (keyCode == BACKSPACE) {
//    if (bufferText.length() > 0) {
//      bufferText = bufferText.substring(0, bufferText.length()-1);
//    }
//  } else if (keyCode == DELETE) {
//    bufferText = "";
//  } else if (keyCode != SHIFT && keyCode != ENTER) {
//    bufferText = bufferText + key;
//  }

//  if (keyCode == ' ') {
//    bufferText = bufferText.substring(0, bufferText.length()-1);
//    bufferText = bufferText + ' ';
//  }


//  if (keyCode == ENTER) {
//    //input = myText;
//    //bufferText = "";
//    doneText = true;
//  }
//}


private void EditText(String txt) {
  bufferText = txt;
}

public class TextInput {
  String text = "";
  boolean active = false;
  String hint = "";
  String label = "";

  public TextInput() {
  }

  public TextInput(String t) {
    this.hint = t;
  }

  public TextInput(String t, String l) {
    this.hint = t;
    this.label = l;
  }

  //Text Input
  public String draw(int x, int y, int w, int h) {
    fill(200);
    textAlign(LEFT, BOTTOM);
    text(label, x, y-21, w, 20);
    if (active) {
      //Edit Text
      fill(c_dark);
      stroke(c_light);
      rect(x, y, w, h);
      noStroke();
      fill(c_text_color);
      
      textAlign(CENTER, CENTER);
      text = bufferText;
      text(text, x, y, w, h);

      if (mouseX >= x && mouseX <= x+w && 
        mouseY >= y && mouseY <= y+h) {
        //Inside
      } else {
        if (clicked) {
          doneText = true;
          //canClick = true;
          active=false;
        }
      }

      if (doneText) {
        text = bufferText;
        active = false;
        doneText = false;
      }
    } else if (mouseX >= x && mouseX <= x+w && 
      mouseY >= y && mouseY <= y+h) {
      fill(c_hover);
      rect(x, y, w, h);
      fill(c_text_color);
      
      textAlign(CENTER, CENTER);
      text(text, x, y, w, h);
      if (clicked && canClick) {
        fill(c_light);
        rect(x, y, w, h);
        fill(255);
        text(text, x, y, w, h);
        EditText(text);
        canClick = false;
        active = true;
      }
    } else {
      fill(c_light);
      stroke(c_dark);
      rect(x, y, w, h);
      fill(c_text_color);
      
      textAlign(CENTER, CENTER);
      text(text, x, y, w, h);
      active = false;
    }
    if (text.length() == 0) {
      fill(150);
      
      textAlign(CENTER, CENTER);
      text(hint, x, y, w, h);
    }
    return text;
  }

  public String getText() {
    return text;
  }
}


//c_mid
public void beginCard(String card_title, int x, int y, int w, int h) {

  noStroke();
  //Shadow
  fill(0, 0, 0, 15);
  rect(x+5, y+5, w, h);
  fill(c_light);
  rect(x, y, w, 40, 2, 2, 0, 0);
  
  textAlign(CENTER, CENTER);
  fill(c_text_color);
  text(card_title, x, y, w, 40);
  fill(c_light);

  rect(x, y+40, w, h-40, 0, 0, 2, 2);

  card_h = h-40;
  card_w = w;
  card_x = x;
  card_y = y+40;
  //uiLight();
}

public void beginCard(int x, int y, int w, int h) {
  noStroke();
  fill(c_mid);

  rect(x, y, w, h);

  card_h = h;
  card_w = w;
  card_x = x;
  card_y = y;
  //uiDark();
}

public void endCard() {
  card_h = 0;
  card_w = 0;
  card_y = 0;
  card_x = 0;
}

//Toggle
public boolean Toggle(boolean value, int x, int y, int w, int h) {
  fill(c_dark);
  stroke(c_light);
  rect(x, y, w, h, h/2);
  int pos = 0;
  if (value)
    pos = w-h;
  //Hover
  if (mouseX >= x && mouseX <= x+w && mouseY >= y && mouseY <= y+h)
  {

    noStroke();

    fill(red(c_hover), green(c_hover), blue(c_hover), 100);  
    ellipse(x+h/2+pos, y+h/2, h-2, h-2);
    fill(c_hover);
    ellipse(x+h/2+pos, y+h/2, h-8, h-8);
    noStroke();
    if (clicked && canClick) {
      value = !value;
      canClick = false;
      return value;
    }
  } 
  //Normal
  else {
    fill(c_light);
    stroke(c_light);
    ellipse(x+h/2+pos, y+h/2, h-8, h-8);
  }


  return value;
}

public boolean Toggle(boolean value, int x, int y) {
  return Toggle(value, x, y, 60, 30);
}

//Toggle
public boolean RadioButton(boolean value, int x, int y, int w, int h) {
  fill(c_dark);
  stroke(c_light);
  rect(x, y, w, h, h/2);
  int pos = 0;
  if (value)
    pos = w-h;
  //Hover
  if (mouseX >= x && mouseX <= x+w && mouseY >= y && mouseY <= y+h)
  {
    fill(c_light);
    stroke(c_hover);
    ellipse(1+x+h/2+pos, y+h/2, h-2, h-2);
    noStroke();
    if (clicked && canClick) {
      value = !value;
      canClick = false;
      return value;
    }
  } 
  //Normal
  else {


    fill(c_light);
    stroke(c_light);
    ellipse(x+h/2+pos, y+h/2, h-8, h-8);
  }


  return value;
}

public boolean Toggle(String text, boolean value, int x, int y, int w, int h) {
  
  fill(255);
  textAlign(LEFT, CENTER);
  text(text, x, y, w, h);
  int pos_x = (int)textWidth(text);
  return Toggle(value, x+10+pos_x, y, 60, 30);
}

/*--- Slider ---
 v1.0
 */

//Basic Slider from 0f to 1f
public float Slider(float min, float max, float value, int x, int y, int w, int h) {
  noStroke();
  fill(c_light);
  rect(x, y+h/2, w, 4, 2);
  float pos = map(value, min, max, 0, w);
  fill(c_hover);
  rect(x, y+h/2, pos, 4, 2);

  //Hover
  if (mouseX >= x && mouseX <= x+w && mouseY >= y && mouseY <= y+h)
  {
    fill(c_hover);
    if (mousePressed) {
      pos = mouseX;
      value = map(pos, x, x+w, min, max);
      fill(red(c_hover), green(c_hover), blue(c_hover), 100);
      ellipse(pos, y+h/2, h, h); 
      fill(c_hover);
      ellipse(pos, y+h/2, h-8, h-8);
    } else {
      fill(red(c_hover), green(c_hover), blue(c_hover), 50);
      ellipse(pos+x, y+h/2, h, h); 
      fill(c_hover);
      ellipse(pos+x, y+h/2, h-8, h-8);
    }
  } 
  //Normal
  else {
    noStroke();
    fill(c_hover);
    ellipse(pos+x, y+h/2, h-8, h-8);
  }

  return value;
}

//Basic Slider with Tooltip
public float Slider(float min, float max, float value, int x, int y, int w, int h, char tooltip) {
  noStroke();
  fill(c_light);
  rect(x, y+h/2, w, 4, 2);
  float pos = map(value, min, max, 0, w);
  fill(c_hover);
  rect(x, y+h/2, pos, 4, 2);

  //Hover
  if (mouseX >= x && mouseX <= x+w && mouseY >= y && mouseY <= y+h)
  {

    fill(c_hover);
    if (mousePressed) {
      stroke(c_hover);
      pos = mouseX;
      value = map(pos, x, x+w, min, max);
      fill(red(c_hover), green(c_hover), blue(c_hover), 100);
      ellipse(pos, y+h/2, h, h); 
      fill(c_hover);
      ellipse(pos, y+h/2, h-8, h-8);

      //Tooltip
      if (tooltip=='%') {
        String s = (int)(value*100)+"%";
        Tooltip(s, (int)(pos), y);
      } else if (tooltip=='#') {
        String s = str((int)value);
        Tooltip(s, (int)(pos), y);
      }
    } else {
      fill(red(c_hover), green(c_hover), blue(c_hover), 50);
      ellipse(pos+x, y+h/2, h, h); 
      fill(c_hover);
      ellipse(pos+x, y+h/2, h-8, h-8);
    }
  } 
  //Normal
  else {
    noStroke();
    fill(c_hover);
    ellipse(pos+x, y+h/2, h-8, h-8);
  }

  return value;
}

public float Slider(String label, float min, float max, float value, int x, int y, int w, int h) {
  int w2 = 0;
  
  float tw = textWidth(label);
  
  fill(255);
  textAlign(LEFT, CENTER);
  text(label, x, y, tw, h);
  w2 = (int)(w-tw-15);
  return Slider(min, max, value, (int)(tw+x+15), y, w2, h);
}

public float Slider(String label, float min, float max, float value, int x, int y, int w, int h, char tooltip) {
  int w2 = 0;
  
  float tw = textWidth(label);
  
  fill(255);
  textAlign(LEFT, CENTER);
  text(label, x, y, tw, h);
  w2 = (int)(w-tw-15);
  return Slider(min, max, value, (int)(tw+x+15), y, w2, h, tooltip);
}

//Minimal Slider
public float Slider(float value, int x, int y) {
  return Slider(0f, 1f, value, x, y, s_big, s_height);
}

public float Slider(float value, int x, int y, char t) {
  return Slider(0f, 1f, value, x, y, s_big, s_height, t);
}

public float Slider(String label, float value, int x, int y) {
  return Slider(label, 0f, 1f, value, x, y, s_big, s_height);
}

public float Slider(String label, float value, int x, int y, char t) {
  return Slider(label, 0f, 1f, value, x, y, s_big, s_height, t);
}

public float Slider(String label, float value, int x, int y, int w, int h) {
  return Slider(label, 0f, 1f, value, x, y, w, h);
}

public float Slider(float value, int x, int y, int w, int h) {
  return Slider(0f, 1f, value, x, y, w, h);
}

public float Slider(float value, int x, int y, int w, int h, char t) {
  return Slider(0f, 1f, value, x, y, w, h, t);
}

public float Slider(float min, int max, int value, int x, int y, int w, int h) {
  return Slider((float) min, (float) max, value, x, y, w, h);
}
class Obstacle {
  PVector pos, vel; 
  float cWidth, cHeight;
  float visual=random(-1, 2);
  int spin= (int)random(0, 180);
  float direction= random(-1, 2);
  float FPS=random(60/frameRate, 180/frameRate);

  //CONSTRUCTOR
  Obstacle(PVector pos) {
    this.pos = pos;
    //cWidth=random(width/10, width/3); 
    cWidth=width/10;
    cHeight=cWidth;
    vel = new PVector(random(-width/240, width/240), random(width/240, width/80));
  }

  public void update() {
    pos.add(vel);

    //screenbox
    //if (pos.x+cWidth/2<0) pos.x=width+(cWidth/2);
    //else if (pos.x-cWidth/2>width) pos.x=0-(cWidth/2);
    //if (pos.y+cHeight/2<0) pos.y=height+(cHeight/2);
    //else if (pos.y-cHeight/2>height) pos.y=0-(cHeight/2);

    if (direction<0) {
      spin-=FPS;
    } else if (direction>=0&&direction<1) {
    } else {
      spin+=FPS;
    }
  }

  public void drawMe() {
    pushMatrix();
    translate(pos.x, pos.y);
    rotate(radians(spin));
    imageMode(CENTER);
    if (visual<0) {
      image(obstacle1, 0, 0);
    } else if (visual>=0&&visual<1) {
      image(meteor1, 0, 0);
    } else {
      image(meteor2, 0, 0);
    }
    popMatrix();
  }
}
class Obstacle2 extends Obstacle {
  float visual=random(-1, 2);
  
  //CONSTRUCTOR
  Obstacle2(PVector pos) {
    super(pos);
    this.pos = pos;
    //cWidth=random(width/10, width/3); 
    cWidth=width/3;
    cHeight=cWidth;
    vel = new PVector(random(-width/240, width/240), random(width/240, width/80));
  }

  public void update() {
    super.update();
  }

  public void drawMe() {
    pushMatrix();
    translate(pos.x, pos.y);
    rotate(radians(spin));
    imageMode(CENTER);
    if (visual<0) {
      image(obstacleBig, 0, 0);
    } else if (visual>=0&&visual<1) {
      image(meteor3, 0, 0);
    } else {
      image(meteor4, 0, 0);
    }
    popMatrix();
  }
}
class Pack {
  PVector pos, vel;
  float cWidth, cHeight;
  int spin= (int)random(0, 180);
  float direction= random(-1, 1);
  float FPS=random(60/frameRate, 180/frameRate);

  //CONSTRUCTOR
  Pack(PVector pos, PVector vel) {
    this.pos = pos;
    this.vel = vel;
    cWidth=width/8;
    cHeight=width/10;
  }

  public void update() {
    pos.add(vel);

    if (pos.x+cWidth<0||pos.x-cWidth>width||pos.y+cHeight<0||pos.y-cHeight>height) {
      pos.x=-100;
      pos.y=-100;
      vel.x=0;
      vel.y=0;
    }

    if (direction<0) {
      spin-=FPS;
    } else {
      spin+=FPS;
    }
  }

  public void drawMe() {
    pushMatrix();
    translate(pos.x, pos.y);
    rotate(radians(spin));
    imageMode(CENTER);
    image(packtop, 0, 0);
    popMatrix();
  }
}
class Particles {
  PVector pos, vel; 
  float cWidth, cHeight;
  int timer;

  //CONSTRUCTOR
  Particles(PVector pos, PVector vel) {
    this.pos = pos;
    this.vel = vel;
    cWidth=width/120;
    cHeight=cWidth;
    timer=0;
  }

  public void update() {
    pos.add(vel);
    timer++;
    cWidth+=.5f;
  }

  public void drawMe() {
    fill(255, map(timer, 0, 15, 255, 0));
    rectMode(CENTER);
    if (mouseX>player.location.x+player.cWidth/2) {
      rect(pos.x-player.cWidth/4, pos.y+player.cHeight/2, cWidth, cWidth);
      rect(pos.x-player.cWidth/4, pos.y-player.cHeight/4, cWidth, cWidth);
    } else if (mouseX<player.location.x-player.cWidth/2) {
      rect(pos.x+player.cWidth/4, pos.y+player.cHeight/2, cWidth, cWidth);
      rect(pos.x+player.cWidth/4, pos.y-player.cHeight/4, cWidth, cWidth);
    } 
    if (mouseY>player.location.y+player.cHeight/2) {
      rect(pos.x-player.cWidth/4, pos.y-player.cHeight/4, cWidth, cWidth);
      rect(pos.x+player.cWidth/4, pos.y-player.cHeight/4, cWidth, cWidth);
    } else if (mouseY<player.location.y-player.cHeight/2) {
      rect(pos.x-player.cWidth/4, pos.y+player.cHeight/2, cWidth, cWidth);
      rect(pos.x+player.cWidth/4, pos.y+player.cHeight/2, cWidth, cWidth);
    } 
    if (mouseX>=player.location.x-player.cWidth/2&&mouseX<=player.location.x+player.cWidth/2&&mouseY>=player.location.y-player.cHeight/2&&mouseY<=player.location.y+player.cHeight/2) {
      rect(pos.x-player.cWidth/4, pos.y+player.cHeight/2, cWidth, cWidth);
      rect(pos.x-player.cWidth/4, pos.y-player.cHeight/4, cWidth, cWidth);
      rect(pos.x+player.cWidth/4, pos.y+player.cHeight/2, cWidth, cWidth);
      rect(pos.x+player.cWidth/4, pos.y-player.cHeight/4, cWidth, cWidth);
    }
  }
}
class Player {
  float cWidth=width/8, cHeight=width/10;
  int startTimer=0;

  //movement
  PVector location;
  PVector velocity;
  PVector acceleration;
  float topSpeed;

  //health and air
  int maxHealth=100;
  float health=maxHealth;
  int invincibleTimer=0;
  int spin=0;

  //Constructor
  Player() {
    location = new PVector(width/2, height/4*3);
    velocity = new PVector(0, 0);
    topSpeed = width/80;
  }

  //collision with objects
  public boolean detectHit(Obstacle other) {
    if (location.x + this.cWidth/2 > other.pos.x -other.cWidth/2.5f &&
      location.x - this.cWidth/2 < other.pos.x +other.cWidth/2.5f &&
      location.y + this.cHeight/2 > other.pos.y -other.cHeight/2.5f && 
      location.y - this.cHeight/2 < other.pos.y +other.cHeight/2.5f) { 
      return true;
    }
    return false;
  }

  public void hit() {    
    if (health >= 20) { 
      health-=20;
    } else if (health==0||health==-1) {
      velocity.x=velocity.x*2;
      velocity.y=velocity.y*2;
      velocity.limit(topSpeed);
      health=-1;
    } else { 
      health=0;
    }
    if (invincibleTimer == 0) { 
      invincibleTimer = 60;
      //air explosion
      for (int p=0; p<10; p++) {
        PVector spawnVector = new PVector(player.location.x+random(-player.cWidth/2, player.cWidth/2), player.location.y+random(-player.cHeight/2, player.cHeight/2));
        PVector velVector = new PVector(random(-width/80, width/80), random(-width/80, width/80));
        particles.add(new Particles(spawnVector, velVector));
      }
    }
  }

  public void update() {
    //cWidth = map(pos.mag(), 0, sqrt(sq(width/2)+sq(height/2)), 1, width/25+height/25);
    //cHeight = map(pos.mag(), 0, sqrt(sq(width/2)+sq(height/2)), 1, width/50+height/50);

    //movement
    if ((gameState!=0&&gameState<2)&&mousePressed&&health>0&&(invincibleTimer==0) || mouseY<height-width/4&&mousePressed&&health>0&&(invincibleTimer==0)) {
      PVector mouse = new PVector(mouseX, mouseY);
      PVector acceleration = PVector.sub(mouse, location);
      // Set magnitude of acceleration
      acceleration.setMag(0.15f*(width/480)); //<<-----needs to adjust depending on screen size;
      // Velocity changes according to acceleration
      velocity.add(acceleration);
      // Limit the velocity by topspeed
      velocity.limit(topSpeed);
      // Location changes by velocity
      location.add(velocity);
      health-=.5f;
    } 
    location.add(velocity);

    //screenbox
    if (gameState!=-1) {
      if (location.x+cWidth/2<0) location.x=width+(cWidth/2-1);
      else if (location.x-cWidth/2>width) location.x=0-(cWidth/2+1);
      if (location.y-cHeight/4<0) velocity.y=velocity.y*-1;
      else if (location.y+cHeight/4>height) velocity.y=velocity.y*-1;
    } else {
      if (location.x+cWidth<0||location.x-cWidth>width||location.y+cHeight<0||location.y-cHeight>height) {
        location.x=-100;
        location.y=-100;
        velocity.x=0;
        velocity.y=0;
      }
    }
  }

  public void drawMe() {
    noStroke();
    fill(255);
    imageMode(CENTER);
    rectMode(CENTER);
    if (location.x==width/2 && location.y==height/4*3) {
      if (startTimer<10) image(top, location.x, location.y);
      else if (startTimer<=20) {
        image(top, location.x, location.y);
        image(top2, location.x, location.y);
      }
      seconds=0;
      frames=0;
      minutes=0;
      dodged=0;
      obstacleMax=5;
      gameState=0;
    } else if (invincibleTimer > 0||gameState==-1) { 
      pushMatrix();
      translate(location.x, location.y);
      rotate(radians(spin));
      spin=spin+6;
      if (gameState==1) {
        if (startTimer<10) image(front, 0, 0);
        else if (startTimer<=20) {
          image(front2, 0, 0);
        }
      } else {
        if (startTimer<10) image(front3, 0, 0);
        else if (startTimer<=20) {
          image(front4, 0, 0);
        }
      }
      popMatrix();
      invincibleTimer--;
    } else if (gameState>1) {
      seconds=0;
      frames=0;
      minutes=0;
      dodged=0;
      obstacleMax=5;
    } else {
      image(top, location.x, location.y);
      spin=0;
      gameState=1;
    }
    if (startTimer>=20) startTimer=0;
    startTimer++;
  }
}
class Stars {
  PVector pos, vel; 
  float redColor;
  int starColor = color(redColor=random(50, 255), redColor, 0);
  float starSize = random(0, width/120);

  //CONSTRUCTOR
  Stars(PVector pos) {
    this.pos = pos;
    vel = new PVector(0, random(0, .05f));
  }

  public void update() {
    //pos.add(vel);

    //screenbox
    if (pos.x<0) pos.x=width;
    else if (pos.x>width) pos.x=0;
    if (pos.y<0) pos.y=height;
    else if (pos.y>height) pos.y=0;
  }

  public void drawMe() {
    fill(starColor);
    noStroke();
    rectMode(CORNER);
    rect(pos.x, pos.y, starSize, starSize);
  }
}
  public void settings() {  fullScreen(); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "EMUrgency" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
