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

  void update() {
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

  void drawMe() {
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