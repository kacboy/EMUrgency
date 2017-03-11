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

  void update() {
    super.update();
  }

  void drawMe() {
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