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

  void update() {
    pos.add(vel);
    timer++;
    cWidth+=.5;
  }

  void drawMe() {
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