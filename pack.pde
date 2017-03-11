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

  void update() {
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

  void drawMe() {
    pushMatrix();
    translate(pos.x, pos.y);
    rotate(radians(spin));
    imageMode(CENTER);
    image(packtop, 0, 0);
    popMatrix();
  }
}