class Stars {
  PVector pos, vel; 
  float redColor;
  color starColor = color(redColor=random(50, 255), redColor, 0);
  float starSize = random(0, width/120);

  //CONSTRUCTOR
  Stars(PVector pos) {
    this.pos = pos;
    vel = new PVector(0, random(0, .05));
  }

  void update() {
    //pos.add(vel);

    //screenbox
    if (pos.x<0) pos.x=width;
    else if (pos.x>width) pos.x=0;
    if (pos.y<0) pos.y=height;
    else if (pos.y>height) pos.y=0;
  }

  void drawMe() {
    fill(starColor);
    noStroke();
    rectMode(CORNER);
    rect(pos.x, pos.y, starSize, starSize);
  }
}