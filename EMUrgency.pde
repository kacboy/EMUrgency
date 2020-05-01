import android.os.Environment;
import apwidgets.*;
import processing.sound.*;

//sound
SoundFile musicFile;
//SoundFile airSound;
boolean music=true;
boolean sfx=true;

//saving
String directory = new String(Environment.getExternalStorageDirectory().getAbsolutePath()) + "/EMUrgency";
String myfn="EMUrgency.txt";    

PFont nokia;
PImage earth, moon, logo, airSmall, airBig, obstacle1, obstacleBig, meteor1, meteor2, meteor3, meteor4, top, top2, front, front2, front3, front4, packtop, credits, help, sound1, sound2;
color logoC = color(random(0, 255), random(0, 200), random(0, 200));

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

void setup() {
  orientation(PORTRAIT); 
  fullScreen(); 
  //size(displayWidth, displayHeight);
  //size(480, 853);
  //size(540, 960);
  //size(480, 720);
  frameRate(30); //<--check player acceleration if you change this

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
  top2.resize(0, (int)(player.cHeight*1.45));
  packtop.resize((int)player.cWidth, 0);
  airSmall.resize(0, (int)(width/20));
  airBig.resize(0, (int)(width/11));
  obstacle1.resize(width/10, 0);
  obstacleBig.resize(width/3, 0);
  meteor1.resize(width/10, 0);
  meteor2.resize(width/10, 0);
  meteor3.resize(width/3, 0);
  meteor4.resize(width/3, 0);

  //sound
  //airSound = new SoundFile(this, "blip.mp3");
  musicFile = new SoundFile(this, "aquanaut.mp3");
  loadData();
  if (!musicFile.isPlaying()&&(music)) {
    musicFile.jump(random(0,119));
    musicFile.loop();
  }

  //background
  earth=loadImage("earth.png");
  moon=loadImage("moon.png");
  earth.resize((int)random(width/2, height/4*3), 0);
  moon.resize((int)random(width/12, width/6), 0);
  earthX=random(0-width/2, width*1.5);
  earthY=random(height/2, height*1.5);
  for (int s=0; s<150; s++) {
    PVector spawnVector = new PVector(random(0, width), random(0, height));
    stars.add(new Stars(spawnVector));
  }
}

void draw() { 
  //background
  background(0);
  for (int s=0; s<stars.size(); s++) {
    stars.get(s).drawMe();
  }
  imageMode(CENTER);
  image(moon, earthY-width*1.25, earthX+width/2);
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

  println("music:"+music+" sfx:"+sfx+" gameState:"+gameState+" obstacles:"+obstacle.size(), "dodged:"+dodged, "obstacleMax:"+obstacleMax);
}

void obstacleSpawn() {
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
  for (int o=obstacle.size()-1; o>=0; o--) {
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
      obstacleMax+=dodged*.0025;
    }
  }
  //obstacle2 drawing+hit
  for (int o=obstacle2.size()-1; o>=0; o--) {
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
      obstacleMax+=dodged*.0025;
    }
  }
}

void airSpawn() {
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
  for (int a=air.size()-1; a>=0; a--) {
    air.get(a).update();
    air.get(a).drawMe();
    //player hit
    if (player.detectHit(air.get(a)) && player.health!=-1 && (player.location.x!=width/2&&player.location.y!=height/4*3)) {
      if ((sfx)) {
        //airSound.play();
      }
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
  for (int a=air2.size()-1; a>=0; a--) {
    air2.get(a).update();
    air2.get(a).drawMe();
    //player hit
    if (player.detectHit(air2.get(a)) && player.health!=-1 && (player.location.x!=width/2&&player.location.y!=height/4*3)) {
      if ((sfx)) {
        //airSound.play();
      }
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

void particlesSpawn() {
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

void HUD() {
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
    fill(255, 255, 255);
    if (minutes>=minsHS&&seconds>secsHS||minutes>minsHS) {
      text("NEW HIGH SCORE", width/2, height/5*2);
    }
    saveData();

    //reset button
    noStroke();
    rectMode(CORNER);
    if (Button("RESET", width/2-width/8, height-width/5, width/4, width/8)&&player.health<=0) {
      musicFile.stop();
      gameState=0;
      spawnPack=0;
      setup();
    }
    text("AIR:00.0 | TIME:"+minutes+":"+nf(seconds, 2)+" | HS:"+minsHS+":"+nf(secsHS, 2), width/2, width/12);
  } else if (gameState==0) {
    //start menu
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
      musicFile.stop();
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
      musicFile.stop();
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
      musicFile.stop();
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

void timers() {
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

void helpMenu() {
  fill(255);
  //text("HELP", width/2, width/12);
  text("AIR:100.0 | TIME:0:00 | HS:"+minsHS+":"+nf(secsHS, 2), width/2, width/12);
  imageMode(CENTER);
  image(help, width/2, height/20*9);
}

void settingsMenu() {
  logo();
  fill(255);
  text("SETTINGS", width/2, width/12);
  rectMode(CORNER);
  imageMode(CORNER);
  if (Button("MUSIC", width/4, height/2-width/5, width/4, width/8)) {
    if ((music)) {
      music=false;
      musicFile.stop();
    } else {
      music=true;
    }
    if (!musicFile.isPlaying()){
      musicFile.loop();
    }
    minutes=-1;
    saveData();
  }
  if (Button("SOUNDS", width/4, height/2, width/4, width/8)) {
    if ((sfx)) {
      sfx=false;
    } else {
      sfx=true;
      minutes=-1;
      saveData();
    }
  }
  if ((music)) {
    image(sound2, width/2+width/12, height/2-width/5);
  } else {
    image(sound1, width/2+width/12, height/2-width/5);
  }
  if ((sfx)) {
    image(sound2, width/2+width/12, height/2);
  } else {
    image(sound1, width/2+width/12, height/2);
  }
  if (Button("RESET HS", width/2-width/8, height/2+width/5, width/4, width/8)) {
    saveData();
  }
  imageMode(CENTER);
}

void credits() {
  logo();
  fill(255);
  textSize(width/25);
  text("CREDITS", width/2, width/12);
  text("SOUND UPDATE v1.1", width/2, height/2-width/10);
  //text("VERSION 0."+((year()-2016)+(30*month()-8)+day()), width/2, height/2-width/10);
  text("EXTRAVEHICULAR MOBILITY UNIT", width/2, height/2-width/3); 
  textSize(width/30);
  text("ROUTINE GONE EXTREMELY NOT COOL YO", width/2, height/2-width/3+width/15);
  imageMode(CORNER);
  image(credits, width/2-credits.width/2, height/2);
  textSize(width/25);
}

void logo() {
  fill(logoC);
  rectMode(CENTER);
  image(logo, width/2, height/4);
  //rect(width/2, height/4*.98, width/3*1.98, height/17);
  //rect(width/4*2.05, height/4*1.08, width/22, height/35);
  //rect(width/4*3.165, height/4*1.08, width/22, height/35);
}

void loadData() {
  ////String[] data = loadStringsFromResourceNamed("data.txt"); 
  ////String data[] = loadStrings("data.txt");
  ////minsHS=int(data[0]);
  ////secsHS=int(data[1]);
  //String lines[];
  //File afile = new File(directory+"/"+myfn);
  //if (afile.exists()) {
  //  lines = loadStrings(afile.getAbsoluteFile());
  //  println(lines.length, lines);
  //  if (lines.length==1) {
  //    lines = split(lines[0], '$');
  //  } 
  //  minsHS=int(lines[1]);
  //  secsHS=int(lines[2]);
  //  music=int(lines[3]);
  //  sfx=int(lines[4]);
  //} else {
  //  lines=null;
  //}
}

void saveData() {
  //if (minutes>=minsHS&&seconds>secsHS||minutes>minsHS||minutes==0&&seconds==0) {
  //  String save = "$"+str(minutes)+"$"+str(seconds)+"$"+str(music)+"$"+str(sfx);
  //  //String[] saved = split(save, '$');
  //  //saveStrings("data.txt", saved);    
  //  PrintWriter  output = createWriter(directory + "/" + "EMUrgency.txt"); 
  //  output.println(save);
  //  output.flush();
  //  output.close();
  //} else if (minutes<0) {
  //  String save = "$"+str(minsHS)+"$"+str(secsHS)+"$"+str(music)+"$"+str(sfx);
  //  //String[] saved = split(save, '$');
  //  //saveStrings("data.txt", saved);    
  //  PrintWriter  output = createWriter(directory + "/" + "EMUrgency.txt"); 
  //  output.println(save);
  //  output.flush();
  //  output.close();
  //}
}

//close the app
void onPause() {
  super.onPause();
  System.exit(0);
}