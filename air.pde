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

  void update() {
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

  void drawMe() {
    pushMatrix();
    translate(pos.x, pos.y);
    rotate(radians(spin));
    imageMode(CENTER);
    image(airSmall, 0, 0);
    popMatrix();
  }
}