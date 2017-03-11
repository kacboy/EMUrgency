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
  boolean detectHit(Obstacle other) {
    if (location.x + this.cWidth/2 > other.pos.x -other.cWidth/2.5 &&
      location.x - this.cWidth/2 < other.pos.x +other.cWidth/2.5 &&
      location.y + this.cHeight/2 > other.pos.y -other.cHeight/2.5 && 
      location.y - this.cHeight/2 < other.pos.y +other.cHeight/2.5) { 
      return true;
    }
    return false;
  }

  void hit() {    
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

  void update() {
    //cWidth = map(pos.mag(), 0, sqrt(sq(width/2)+sq(height/2)), 1, width/25+height/25);
    //cHeight = map(pos.mag(), 0, sqrt(sq(width/2)+sq(height/2)), 1, width/50+height/50);

    //movement
    if ((gameState!=0&&gameState<2)&&mousePressed&&health>0&&(invincibleTimer==0) || mouseY<height-width/4&&mousePressed&&health>0&&(invincibleTimer==0)) {
      PVector mouse = new PVector(mouseX, mouseY);
      PVector acceleration = PVector.sub(mouse, location);
      // Set magnitude of acceleration
      acceleration.setMag(0.15*(width/480)); //<<-----needs to adjust depending on screen size;
      // Velocity changes according to acceleration
      velocity.add(acceleration);
      // Limit the velocity by topspeed
      velocity.limit(topSpeed);
      // Location changes by velocity
      location.add(velocity);
      health-=.5;
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

  void drawMe() {
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