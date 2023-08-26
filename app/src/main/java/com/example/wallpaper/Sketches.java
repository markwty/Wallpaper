package com.example.wallpaper;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;
import processing.core.PVector;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;

class Sketches
{
    Sketches() {}
    PApplet GetSketch(int wallpaper_number, boolean sensor_enabled)
    {
        switch(wallpaper_number)
        {
            case 0:
                return new TreeSketch(sensor_enabled);
            case 1:
                return new SnowflakeSketch(sensor_enabled);
            case 2:
                return new SeasonsSketch(sensor_enabled);
            case 3:
                return new WormSketch(sensor_enabled);
            case 4:
                return new GeckoSketch(sensor_enabled);
            case 5:
                return new PuffSketch(sensor_enabled);
            case 6:
                return new BubbleTeaSketch(sensor_enabled);
            case 7:
                return new TentacleSketch(sensor_enabled);
            case 8:
                return new BlueGrowthSketch(sensor_enabled);
            default:
                return new TreeSketch(false);
        }
    }
}
class TreeSketch extends Sketch_SensorEnabled
{
    TreeSketch(boolean sensor_enabled)
    {
        super(sensor_enabled);
    }

    private float length, angle, ratio;

    private float shiftFactor;

    public void setup()
    {
        length = height/4;
        stroke(255);
    }
    public void draw()
    {
        background(0);
        angle = map(constrain(pitch, -PI/4, -PI/8), -PI/4, -PI/8, PI/10, PI*0.9F);
        ratio = map(constrain(tilt, -PI/8, PI/8), -PI/8, PI/8, 0.4F, 0.6F);
        translate(width/2, height);
        branch(length);
    }

    private void branch(float length){
        line(0, 0, 0, -length);
        translate(0, -length);
        if (length > 4)
        {
            push();
            rotate(angle);
            branch(length * ratio);
            pop();
            push();
            translate(0, shiftFactor * length);
            rotate(-1 * angle);
            branch(length * ratio);
            pop();
        }
    }
    @Override
    public void loadPrefs(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        shiftFactor = Float.parseFloat(prefs.getString("shiftFactor", "0"));
        shiftFactor = constrain(shiftFactor, 0, 1);
    }
}

class SnowflakeSketch extends Sketch_SensorEnabled
{
    SnowflakeSketch(boolean sensor_enabled)
    {
        super(sensor_enabled);
    }

    static private PImage img;
    static private boolean saved = false;

    private int numberOfParts, red, green, blue;

    public void setup()
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean previewed = prefs.getBoolean("Previewed", false);
        background(0);
        if (saved && !previewed)
        {
            noLoop();
            onStop();
            image(img, 0, 0);
        }
        else
        {
            img = createImage(width, height, RGB);
        }
        saved = false;
    }

    public void draw()
    {
        if (pitch < -PI/3)
        {
            noLoop();
            onStop();
            img.loadPixels();
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int px = get(x, y);
                    img.pixels[x + y * width] = px;
                }
            }
            img.updatePixels();
            textAlign(CENTER);
            textSize(100);
            fill(0, 102, 153);
            text("Locked", width / 2, height / 2);
            saved = true;
        }
        translate(width / 2, height / 2);
    }

    public void mouseDragged()
    {
        int mx = mouseX - width / 2;
        int my = mouseY - height / 2;
        int pmx = pmouseX - width / 2;
        int pmy = pmouseY - height / 2;
        stroke(red, green, blue, 100);
        float angle = 360 / numberOfParts;
        for (int i = 0; i < numberOfParts; i++) {
            rotate(radians(angle));
            float dist = dist(mx, my, pmx, pmy);
            strokeWeight(map(constrain(dist, 0, 16), 0, 16, 30, 2));
            line(mx, my, pmx, pmy);
            push();
            scale(1, -1);
            line(mx, my, pmx, pmy);
            pop();
        }
    }
    @Override
    public void loadPrefs(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        numberOfParts = Integer.valueOf(prefs.getString("numberOfParts", "6"));
        red = prefs.getInt("red", 6);
        green = prefs.getInt("green", 6);
        blue = prefs.getInt("blue", 6);
    }
}

class StarsSketch extends Sketch_SensorEnabled
{
    StarsSketch(boolean sensor_enabled)
    {
        super(sensor_enabled);
    }

    private PGraphics lay_Stars;
    private PImage img;
    private float speed, max_speed = 200;
    private boolean paused=false;
    private Star [] arr_Star=new Star[10];
    private block_Letter word;

    public void setup(){
        img = loadImage("blue.jpg");
        img.resize(width, height);
        lay_Stars = createGraphics(width,height);
        for (int i=0;i<arr_Star.length;i++){
            arr_Star[i] = new Star(lay_Stars,4,30);
            arr_Star[i].update(speed);
            arr_Star[i].show();
        }
        word = new block_Letter("supernova", height/2, 40);
    }

    public void draw(){
        speed = map(mouseX, 0, width, -max_speed, max_speed);
        lay_Stars.beginDraw();
        lay_Stars.background(255);
        lay_Stars.endDraw();
        word.show();
        for(Star star:arr_Star){
            if (speed < 0){
                star.update(speed);
                star.cluster();
                star.attract(50);
            } else if (star.increment > 0){
                star.update(speed);
                star.unattract();
            } else{
                star.update(speed);
                star.show();
            }
        }
        image(img, 0, 0);
        image(lay_Stars, 0, 0);
    }

    public void mousePressed(){
        if (!paused)
        {
            noLoop();
            paused=true;
        }
        else
        {
            loop();
            paused=false;
        }
    }

    @Override
    public void loadPrefs(){
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
    }

    class Star{
        private int start_size,max_size,partner,cof_increment;
        private float x,y,z,ori_z,ix,iy,sx,sy,sz,increment,speed,starSize;
        private ArrayList <Integer> arr_Pixels= new ArrayList <>();
        private PGraphics lay_Stars;
        private PShape starShape;

        Star(PGraphics ilay_Stars,int istart_size,int imax_size){
            lay_Stars=ilay_Stars;
            start_size=istart_size;
            x=random(-width,width);
            y=random(-height,height);
            z=random(width);
            partner=-1;
            max_size=imax_size;
            //color(255,215,0);
            starShape = loadShape("star.svg");
            starShape.scale(10);
            starSize = 240;
        }

        void update(float ispeed){
            if (partner==-1 && speed<0){
                ori_z=z;
            }
            speed=ispeed;
            z = z-speed;
            if (z <= 0){
                z = random(width);
                x = random(-width, width);
                y = random(-height, height);
            }
        }
        void show(){
            sx = map(x/z, -1, 1, 0, width);
            sy = map(y/z, -1, 1, 0, height);
            sz = map(start_size/z, start_size/width, start_size * 0.1F, 2, max_size);
            lay_Stars.beginDraw();
            lay_Stars.shape(starShape, sx, sy, sz/max_size * starSize, sz/max_size * starSize);
            lay_Stars.endDraw();
        }

        void cluster(PGraphics ilay){
            if (partner == -1){
                increment = 0;
                ilay.loadPixels();
                for (int i = 0; i < ilay.pixels.length;i++){
                    if (ilay.pixels[i] == color(255,0,0)){
                        arr_Pixels.add(i);
                    }
                }
                partner = arr_Pixels.get(floor(random(0, arr_Pixels.size() - 0.1F)));
            }
        }

        void cluster(){
            if (partner == -1){
                loadPixels();
                for (int i = 0;i < pixels.length; i++){
                    if (pixels[i] == color(255,0,0)){
                        arr_Pixels.add(i);
                    }
                }
                partner=arr_Pixels.get(floor(random(0,arr_Pixels.size() - 0.1F)));
            }
        }

        void attract(int icof_increment){
            cof_increment = icof_increment;
            sz = map(start_size/z, start_size/width, start_size * 0.1F, 2, max_size);
            ix = sx+(partner % width - sx) * increment/cof_increment;
            iy = sy + (floor(partner/width)-sy) * increment/cof_increment;
            lay_Stars.beginDraw();
            lay_Stars.shape(starShape, ix, iy, sz/max_size * starSize, sz/max_size * starSize);
            lay_Stars.endDraw();
            increment=increment + map(speed, 0, -max_speed,0,1);
            increment=constrain(increment, 0, cof_increment);
        }

        void unattract(){
            sz = map(start_size/z,start_size/width,start_size * 0.1F,2,max_size);
            ix = sx + (partner % width - sx) * increment/cof_increment;
            iy = sy + (floor(partner/width) - sy) * increment/cof_increment;
            lay_Stars.beginDraw();
            lay_Stars.shape(starShape, ix, iy, sz/max_size * starSize, sz/max_size * starSize);
            lay_Stars.endDraw();
            increment = increment - map(speed, 0, max_speed, 0, 1);
            increment = constrain(increment, 0, cof_increment);
            if (increment == 0){
                z = ori_z;
            }
        }
    }

    class block_Letter{
        private PGraphics lay;
        private String word;
        private float y,gap;
        private int [] arr_Blocks=new int [20];
        private ArrayList <Float> arr_Data=new ArrayList <>();
        block_Letter(String iword,float iystart,float igap,PGraphics ilay){
            lay=ilay;
            word=iword;
            gap=igap;
            y=iystart;
        }

        block_Letter(String iword,float iystart,float igap){
            word=iword;
            gap=igap;
            y=iystart;
        }

        void show(){
            int x, i;
            for (x = 0; x < word.length(); x ++) {
                switch(word.substring(x,x+1)){
                    case "a":
                        for (i=0;i<20;i++){
                            if (i==1||i==2||i==4||(i>6 && i<13)||i==15||i==16||i==19){
                                arr_Blocks[i]=1;
                            } else {
                                arr_Blocks[i]=0;
                            }
                        }
                        break;
                    case "b":
                        for (i=0;i<20;i++){
                            if ((i>=0 && i<3)||i==4||(i>6 && i<11)||i==12||(i>14 && i<19)){
                                arr_Blocks[i]=1;
                            } else {
                                arr_Blocks[i]=0;
                            }
                        }
                        break;
                    case "c":
                        for (i=0;i<20;i++){
                            if (i==1||i==2||i==4||i==7||i==8||i==12||i==15||i==17||i==18){
                                arr_Blocks[i]=1;
                            } else {
                                arr_Blocks[i]=0;
                            }
                        }
                        break;
                    case "d":
                        for (i=0;i<20;i++){
                            if ((i>=0 && i<3)||i==4||i==7||i==8||i==11||i==12||(i>14 && i<19)){
                                arr_Blocks[i]=1;
                            } else {
                                arr_Blocks[i]=0;
                            }
                        }
                        break;
                    case "e":
                        for (i=0;i<20;i++){
                            if ((i>=0 && i<5)||(i==10)||i==12||(i>15)){
                                arr_Blocks[i]=1;
                            } else {
                                arr_Blocks[i]=0;
                            }
                        }
                        break;
                    case "f":
                        for (i=0;i<20;i++){
                            if ((i>=0 && i<5)||(i>7 && i<11)||i==12||(i==16)){
                                arr_Blocks[i]=1;
                            } else {
                                arr_Blocks[i]=0;
                            }
                        }
                        break;
                    case "g":
                        for (i=0;i<20;i++){
                            if ((i>0 && i<5)||i==8||(i>9 && i<13)||i==15||(i>16)){
                                arr_Blocks[i]=1;
                            } else {
                                arr_Blocks[i]=0;
                            }
                        }
                        break;
                    case "h":
                        for (i=0;i<20;i++){
                            if (i==0||i==3||i==4||(i>6 && i<13)||i==15||i==16||i==19){
                                arr_Blocks[i]=1;
                            } else {
                                arr_Blocks[i]=0;
                            }
                        }
                        break;
                    case "i":
                        for (i=0;i<20;i++){
                            if ((i>=0 && i<3)||i==5||i==9||i==13||(i>15 && i<19)){
                                arr_Blocks[i]=1;
                            } else {
                                arr_Blocks[i]=0;
                            }
                        }
                        break;
                    case "j":
                        for (i=0;i<20;i++){
                            if ((i>=0 && i<4)||i==7||i==11||i==12||i==15||i==17||i==18){
                                arr_Blocks[i]=1;
                            } else {
                                arr_Blocks[i]=0;
                            }
                        }
                        break;
                    case "k":
                        for (i=0;i<20;i++){
                            if (i==0||i==3||i==4||i==6||i==8||i==9||i==12||i==14||i==16||i==19){
                                arr_Blocks[i]=1;
                            } else {
                                arr_Blocks[i]=0;
                            }
                        }
                        break;
                    case "l":
                        for (i=0;i<20;i++){
                            if (i==0||i==4||i==8||i==12||(i>15)){
                                arr_Blocks[i]=1;
                            } else {
                                arr_Blocks[i]=0;
                            }
                        }
                        break;
                    case "m":
                        for (i=0;i<20;i++){
                            if (i==0||(i>2 && i<9)||i==11||i==12||i==15||i==16||i==19){
                                arr_Blocks[i]=1;
                            } else {
                                arr_Blocks[i]=0;
                            }
                        }
                        break;
                    case "n":
                        for (i=0;i<20;i++){
                            if (i==0||i==3||i==4||i==7||i==8||i==9||i==11||i==12||i==14||i==15||i==16||i==19){
                                arr_Blocks[i]=1;
                            } else {
                                arr_Blocks[i]=0;
                            }
                        }
                        break;
                    case "o":
                        for (i=0;i<20;i++){
                            if (i==1||i==2||i==4||i==7||i==8||i==11||i==12||i==15||i==17||i==18){
                                arr_Blocks[i]=1;
                            } else {
                                arr_Blocks[i]=0;
                            }
                        }
                        break;
                    case "p":
                        for (i=0;i<20;i++){
                            if ((i>=0 && i<3)||i==4||(i>6 && i<11)||i==12||i==16){
                                arr_Blocks[i]=1;
                            } else {
                                arr_Blocks[i]=0;
                            }
                        }
                        break;
                    case "q":
                        for (i=0;i<20;i++){
                            if (i==1||i==4||i==6||i==8||i==10||i==12||i==14||(i>16)){
                                arr_Blocks[i]=1;
                            } else {
                                arr_Blocks[i]=0;
                            }
                        }
                        break;
                    case "r":
                        for (i=0;i<20;i++){
                            if ((i>=0 && i<3)||i==4||(i>6 && i<11)||i==12||i==14||i==16||i==19){
                                arr_Blocks[i]=1;
                            } else {
                                arr_Blocks[i]=0;
                            }
                        }
                        break;
                    case "s":
                        for (i=0;i<20;i++){
                            if ((i>0 && i<5)||i==9||i==10||(i>14 && i<19)){
                                arr_Blocks[i]=1;
                            } else {
                                arr_Blocks[i]=0;
                            }
                        }
                        break;
                    case "t":
                        for (i=0;i<20;i++){
                            if ((i>=0 && i<3)||i==5||i==9||i==13||i==17){
                                arr_Blocks[i]=1;
                            } else {
                                arr_Blocks[i]=0;
                            }
                        }
                        break;
                    case "u":
                        for (i=0;i<20;i++){
                            if (i==0||i==3||i==4||i==7||i==8||i==11||i==12||(i>14)){
                                arr_Blocks[i]=1;
                            } else {
                                arr_Blocks[i]=0;
                            }
                        }
                        break;
                    case "v":
                        for (i=0;i<20;i++){
                            if (i==0||i==3||i==4||i==7||i==8||i==11||i==12||i==15||i==17||i==18){
                                arr_Blocks[i]=1;
                            } else {
                                arr_Blocks[i]=0;
                            }
                        }
                        break;
                    case "w":
                        for (i=0;i<20;i++){
                            if (i==0||i==3||i==4||i==7||i==8||(i>10 && i<17)||i==19){
                                arr_Blocks[i]=1;
                            } else {
                                arr_Blocks[i]=0;
                            }
                        }
                        break;
                    case "x":
                        for (i=0;i<20;i++){
                            if (i==0||i==3||i==4||i==7||i==9||i==10||i==12||i==15||i==16||i==19){
                                arr_Blocks[i]=1;
                            } else {
                                arr_Blocks[i]=0;
                            }
                        }
                        break;
                    case "y":
                        for (i=0;i<20;i++){
                            if (i==0||i==3||i==4||i==7||(i>8 && i<12)||i==15||i==17||i==18){
                                arr_Blocks[i]=1;
                            } else {
                                arr_Blocks[i]=0;
                            }
                        }
                        break;
                    case "z":
                        for (i=0;i<20;i++){
                            if ((i>=0 && i<5)||i==7||i==9||i==10||i==12||(i>15)){
                                arr_Blocks[i]=1;
                            } else {
                                arr_Blocks[i]=0;
                            }
                        }
                        break;
                    default:
                        for (i=0;i<20;i++){
                            arr_Blocks[i]=0;
                        }
                        break;
                }

                if (lay==null){
                    noStroke();
                    fill(255,0,0);
                    for (i=0;i<20;i++){
                        if (arr_Blocks[i]==1){
                            rect((width-gap*word.length())/2+gap*x+i%4*gap/5,y+floor(i/4)*gap/4,gap/5,gap/4);
                            arr_Data.add((width-gap*word.length())/2+gap*x+i%4*gap/5+gap/10);
                            arr_Data.add(y+floor(i/4)*gap/4+gap/8);
                        }
                    }
                }else{
                    lay.beginDraw();
                    lay.noStroke();
                    lay.fill(255,0,0);
                    for (i=0;i<20;i++){
                        if (arr_Blocks[i]==1){
                            lay.rect((width-gap*word.length())/2+gap*x+i%4*gap/5,y+floor(i/4)*gap/4,gap/5,gap/4);
                            arr_Data.add((width-gap*word.length())/2+gap*x+i%4*gap/5+gap/10);
                            arr_Data.add(y+floor(i/4)*gap/4+gap/8);
                        }
                    }
                    lay.endDraw();
                }
            }
        }
    }
}

class SeasonsSketch extends Sketch_SensorEnabled
{
    SeasonsSketch(boolean sensor_enabled)
    {
        super(sensor_enabled);
    }

    private int numLeafs = 3, prev_numFallObjects, prev_mode = 0, mode = 0, prevColor, currColor;
    //0: winter, 1: autumn, 2: spring, 3: summer
    private int[] colors;
    private float colorGradient = 0, speed_factor = 0.4F;
    private float[] ori_angles;
    private boolean cleared = false;
    private PImage snow, raindrop;
    private PImage[] leafs;
    private Ground ground;
    private ArrayList<FallObject> fallObjects = new ArrayList<>();

    private int numFallObjects = 10;

    public void setup(){
        snow = loadImage("snowflake.png");
        leafs = new PImage[numLeafs];
        ori_angles = new float[numLeafs];
        leafs[0] = loadImage("leaf1.png");
        ori_angles[0] = -PI/8;
        leafs[1] = loadImage("leaf2.png");
        ori_angles[1] = PI/4;
        leafs[2] = loadImage("leaf3.png");
        ori_angles[2] = PI/4;
        raindrop = loadImage("raindrop.png");
        for (int i = 0; i < numFallObjects; i++)
        {
            fallObjects.add(new Snow(snow));
        }
        ground = new SnowGround(100);
        colors = new int[4];
        colors[0] = color(0);
        colors[1] = color(247, 151, 98);
        colors[2] = color(170, 242, 0);
        colors[3] = color(55, 202, 206);
        prevColor = colors[0];
    }

    public void draw() {
        if (!cleared && pitch > - PI/8)
        {
            ground.clearGround();
            cleared = true;
        }
        if (pitch < -PI/8)
        {
            cleared = false;
        }
        if (prev_mode != mode)
        {
            prevColor = currColor;
            prev_mode = mode;
            colorGradient = 0;
            int layer_height = ground.layer_height;
            switch (mode)
            {
                case 0:
                    ground = new SnowGround(layer_height);
                    break;
                case 1:
                    ground = new LitterGround(layer_height, ground.lay_Ground, leafs, numLeafs);
                    break;
                case 2:
                    ground = new Pool(layer_height);
                    break;
                case 3:
                    ground = new Dirt(layer_height);
                    break;
            }
        }
        currColor = lerpColor(prevColor, colors[mode], colorGradient, 1);
        background(currColor);
        colorGradient += 0.005;
        if (colorGradient > 1)
        {
            colorGradient = 1;
        }
        /*
        translate(width/2, height/2);
        rotate(constrain(tilt, -PI/4, PI/4));
        translate(-width/2, -height/2);
        */
        stroke(255, 100);
        switch (mode)
        {
            case 0:
                if (prev_numFallObjects < numFallObjects)
                {
                    for (int i = 0; i < numFallObjects - prev_numFallObjects; i++)
                    {
                        fallObjects.add(new Snow(snow));
                    }
                }
                break;
            case 1:
                if (prev_numFallObjects < numFallObjects)
                {
                    for (int i = 0; i < numFallObjects - prev_numFallObjects; i++)
                    {
                        int rand = (int)random(0, numLeafs);
                        fallObjects.add(new Leaf(leafs[rand], ori_angles[rand]));
                    }
                }
                break;
            case 2:
                if (prev_numFallObjects < numFallObjects)
                {
                    for (int i = 0; i < numFallObjects - prev_numFallObjects; i++)
                    {
                        fallObjects.add(new RainDrop(raindrop));
                    }
                }
                break;
            case 3:
                if (prev_numFallObjects < numFallObjects)
                {
                    for (int i = 0; i < numFallObjects - prev_numFallObjects; i++)
                    {
                        fallObjects.add(new RainDrop(raindrop));
                    }
                }
                break;
        }

        if (prev_numFallObjects > numFallObjects)
        {
            fallObjects.subList(0, prev_numFallObjects - numFallObjects).clear();
        }
        prev_numFallObjects = numFallObjects;
        for (int i = 0; i < prev_numFallObjects; i++)
        {
            if (fallObjects.get(i).update()) {
                switch (mode)
                {
                    case 0:
                        if (fallObjects.get(i) instanceof Snow)
                        {
                            fallObjects.get(i).setImage(snow);
                        }
                        else
                        {
                            fallObjects.remove(i);
                            fallObjects.add(i, new Snow(snow));
                        }
                        break;
                    case 1:
                        int rand = (int)random(0, numLeafs);
                        if (fallObjects.get(i) instanceof Leaf)
                        {
                            fallObjects.get(i).setImage(leafs[rand], ori_angles[rand]);
                        }
                        else
                        {
                            fallObjects.remove(i);
                            fallObjects.add(i, new Leaf(leafs[rand], ori_angles[rand]));
                        }
                        break;
                    case 2:
                        if (fallObjects.get(i) instanceof RainDrop)
                        {
                            fallObjects.get(i).setImage(raindrop);
                        }
                        else
                        {
                            fallObjects.remove(i);
                            fallObjects.add(i, new RainDrop(raindrop));
                        }
                        break;
                    case 3:
                        if (fallObjects.get(i) instanceof RainDrop)
                        {
                            fallObjects.get(i).setImage(raindrop);
                        }
                        else
                        {
                            fallObjects.remove(i);
                            fallObjects.add(i, new RainDrop(raindrop));
                        }
                        break;
                }
            }
        }
        ground.show();
    }

    public void mousePressed()
    {
        mode++;
        if (mode == 4)
        {
            mode = 0;
        }
    }

    @Override
    public void loadPrefs() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        numFallObjects = Integer.valueOf(prefs.getString("numFallObjects", "25"));
        numFallObjects = constrain(numFallObjects, 0, 60);
        speed_factor = Float.parseFloat(prefs.getString("speedFactor", "1"));
        speed_factor = constrain(speed_factor, 0.4F, 3F);
    }

    class FallObject
    {
        private int x, y;
        float size, angle, angleChange, factor, ori_angle;
        private PImage object;
        FallObject(PImage iobject)
        {
            size = random(30, 50);
            object = iobject.copy();
            object.resize(0, (int)(3*size));
            //Resized to maximum possible size to reduce time required to copy image
            x = (int)(random(0, width));
            y = 0;
            angle = random(0, PI);
            angleChange = random(0, 0.1F) - 0.05F;
        }
        float getFallDistance()
        {
            return (height - ground.layer_height - 3*size);
        }
        boolean update()
        {
            y += size * speed_factor;
            angle += angleChange * factor;
            if (y > getFallDistance())
            {
                if (mode == 1 && getClass().getSimpleName().equals(Leaf.class.getSimpleName()))
                {
                    ground.setActual(object, angle);
                }
                ground.pile(x);
                size = random(30, 50);
                x = (int)(random(0, width));
                y = 0;
                angle = random(0, PI);
                return true;
            }
            push();
            translate(x, y);
            rotate(angle);
            image(object, 0, 0);
            pop();
            return false;
        }

        void setImage(PImage iobject)
        {
            object = iobject.copy();
            object.resize((int)(3*size), (int)(3*size));
        }

        void setImage(PImage iobject, float iori_angle)
        {
            setImage(iobject);
            ori_angle = iori_angle;
        }
    }

    class Snow extends FallObject {
        Snow(PImage iobject) {
            super(iobject);
            factor = 1;
        }
    }

    class Leaf extends FallObject {
        private int cnt, curr;
        private float half_range = PI/4;
        Leaf(PImage iobject, float ori_angle) {
            super(iobject);
            angle = ori_angle;
            angleChange = random(0, 0.1F) - 0.05F;
            cnt = (int)abs(half_range / angleChange);
            factor = 1;
        }
        float getFallDistance()
        {
            return (height - ground.layer_height - 3*size / 2);
        }
        boolean update()
        {
            boolean re = super.update();
            if (re)
            {
                angle = ori_angle;
            }
            curr++;
            if (curr == cnt)
            {
                factor *= -1;
                curr = 0;
            }
            return re;
        }
    }

    class RainDrop extends FallObject {
        RainDrop(PImage rainDrop) {
            super(rainDrop);
            angle = 0;
            factor = 0;
        }

        boolean update()
        {
            angle = 0;
            return super.update();
        }
    }

    class Ground
    {
        int layer_height, curr_count, rand;
        float diff_height;
        PGraphics lay_Ground;
        Ground(int ilayer_height)
        {
            layer_height = ilayer_height;
            curr_count = 0;
            lay_Ground = createGraphics(width * 2, height + width);
        }
        void drawStart() {}
        void setActual(PImage iobject, float iangle){}
        void drawTop(float x){}
        void pile(float x)
        {
            if (layer_height > height + width/2)
            {
                return;
            }
            diff_height = height + width/2 - layer_height;
            drawTop(x);
            curr_count++;
        }
        void show()
        {
            image(lay_Ground, -width/2, -width/2);
        }
        void clearGround()
        {
            layer_height = 0;
            lay_Ground = createGraphics(width * 2, height + width);
            drawStart();
        }
    }

    class SnowGround extends Ground
    {
        private int increment = 30;
        SnowGround(int ilayer_height)
        {
            super(ilayer_height);
            drawStart();
        }
        void drawStart()
        {
            lay_Ground.beginDraw();
            lay_Ground.fill(200);
            lay_Ground.noStroke();
            lay_Ground.rect(0, height + width/2 - layer_height, width * 2, layer_height + width/2);
            lay_Ground.endDraw();
        }
        void drawTop(float x)
        {
            lay_Ground.beginDraw();
            lay_Ground.beginShape();
            rand = (int)random(5, 15);
            lay_Ground.curveVertex(x + width/2 - rand * 30, diff_height + 30);
            lay_Ground.curveVertex(x + width/2 - rand * 18, diff_height);
            lay_Ground.curveVertex(x + width/2F , diff_height - 50);
            lay_Ground.curveVertex(x + width/2F + rand * 18, diff_height);
            lay_Ground.curveVertex(x + width/2F + rand * 30, diff_height + 30);
            lay_Ground.endShape(CLOSE);
            lay_Ground.endDraw();
        }
        void pile(float x)
        {
            super.pile(x);
            if (curr_count > 15)
            {
                curr_count = 0;
                layer_height += increment;
                lay_Ground.beginDraw();
                lay_Ground.rect(0, diff_height - increment, width * 2, increment);
                lay_Ground.endDraw();
            }
        }
    }

    class LitterGround extends Ground
    {
        private int rand2, numLeafs, increment = 30;
        private float angle, offset;
        private boolean through;
        private PImage curr_leaf;
        private PImage[] leafs;
        LitterGround(int ilayer_height, PGraphics layer, PImage [] ileafs, int isize)
        {
            super(ilayer_height);
            numLeafs = isize;
            leafs = new PImage[numLeafs];
            leafs = ileafs;
            lay_Ground = layer;
            drawStart();
            through = false;
        }
        void drawStart()
        {
            lay_Ground.beginDraw();
            for (int i = 0; i < 20; i++)
            {
                rand = (int)random(60, 100);
                rand2 = (int)random(0, numLeafs);
                lay_Ground.image(leafs[rand2], random(0, width * 2), random(height + width/2 - layer_height, height + width/2 - layer_height + increment), rand, 0);
            }
            lay_Ground.endDraw();
        }
        void setActual(PImage ileaf, float iangle)
        {
            curr_leaf = ileaf;
            angle = iangle;
            through = true;
        }
        void drawTop(float x)
        {
            if (!through)
            {
                curr_leaf = leafs[(int)random(0, numLeafs)].copy();
                curr_leaf.resize((int)random(60, 100), 0);
                angle = random(0, PI * 2);
                offset = curr_leaf.width / 2;
            }
            lay_Ground.beginDraw();
            lay_Ground.push();
            lay_Ground.translate(x + width/2F, diff_height - 10 + offset);
            lay_Ground.rotate(angle);
            lay_Ground.image(curr_leaf, 0, 0);
            lay_Ground.pop();
            lay_Ground.endDraw();
            offset = 0;
        }
        void pile(float x)
        {
            super.pile(x);
            if (curr_count > 15)
            {
                curr_count = 0;
                layer_height += increment;
                lay_Ground.beginDraw();
                for (int i = 0; i < 20; i++)
                {
                    rand = (int)random(60, 100);
                    rand2 = (int)random(0, numLeafs);
                    lay_Ground.image(leafs[rand2], random(0, width * 2), random(diff_height - increment, diff_height - increment * 2), rand, 0);
                }
                lay_Ground.endDraw();
            }
        }
    }

    class Pool extends Ground
    {
        private int increment = 5;
        Pool(int ilayer_height)
        {
            super(ilayer_height);
            drawStart();
        }
        void drawStart()
        {
            lay_Ground.beginDraw();
            lay_Ground.fill(156, 211, 219, 80);
            lay_Ground.noStroke();
            lay_Ground.rect(0, height + width/2 - layer_height, width * 2, layer_height + width/2);
            lay_Ground.endDraw();
        }
        void pile(float x)
        {
            super.pile(x);
            layer_height += increment;
            lay_Ground.beginDraw();
            lay_Ground.rect(0, diff_height - increment, width * 2, increment);
            lay_Ground.endDraw();
            if (curr_count > 30)
            {
                curr_count = 0;
            }
        }
    }

    class Dirt extends Ground
    {
        Dirt(int ilayer_height)
        {
            super(ilayer_height);
            drawStart();
        }
        void drawStart()
        {
            lay_Ground.beginDraw();
            lay_Ground.fill(164, 118, 74);
            lay_Ground.noStroke();
            lay_Ground.rect(0, height + width/2 - layer_height, width * 2, layer_height + width/2);
            lay_Ground.endDraw();
        }
        void pile(float x){}
    }
}

class BallSketch extends Sketch_SensorEnabled
{
    BallSketch(boolean sensor_enabled)
    {
        super(sensor_enabled);
    }

    private int radius;
    private float speed;
    private Ball ball;

    public void setup()
    {
        ball = new Ball(radius, width/2, height - radius, 1);
    }

    public void draw()
    {
        background(0);
        ball.bounce();
        ball.show();
    }

    public void mousePressed()
    {
        ball.setRadius(radius);
        ball.setSpeed(speed);
        ball.setPosition();
    }

    @Override
    public void loadPrefs(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        radius = Integer.valueOf(prefs.getString("ballRadius", "30"));
        radius = constrain(radius, 1, 100);
        speed = Float.parseFloat(prefs.getString("ballSpeed", "1"));
        speed = constrain(speed, 0.1F, 5);
    }

    class Ball
    {
        private int radius, x, y, factor_x = 1, factor_y = 1;
        private float speed, energy = 0, velocity_x = 0, velocity_y = 0;
        private float ratio;
        Ball(int iradius, int ix, int iy, float ispeed)
        {
            radius = iradius;
            x = ix;
            y = iy;
            speed = ispeed;
        }

        void bounce(){
            energy -= 1;
            if (energy > 0)
            {
                if (ratio > 5)
                {
                    velocity_y += 0.5 * speed;
                    velocity_x = 0;
                }
                else
                {
                    velocity_y += 0.1 * ratio * speed;
                    velocity_x += 0.1;
                }
            }
            else
            {
                if (ratio > 5)
                {
                    velocity_y -= 0.5 * speed;
                    velocity_x = 0;
                }
                else
                {
                    velocity_y -= 0.1 * ratio * speed;
                    velocity_x -= 0.1;
                }
                if (velocity_y < 0)
                {
                    velocity_y = 0;
                }
                if (velocity_x < 0)
                {
                    velocity_x = 0;
                }
            }
            y += velocity_y * factor_y;
            x += velocity_x * factor_x;
            if (y < radius)
            {
                factor_y = 1;
            }
            else if (y > height - radius)
            {
                factor_y = -1;
            }
            if (x < radius)
            {
                factor_x = 1;
            }
            else if (x > width - radius)
            {
                factor_x = -1;
            }
        }

        void setRadius(int iradius)
        {
            radius = iradius;
        }

        void setSpeed(float ispeed)
        {
            speed = ispeed;
        }

        void setPosition()
        {
            int next_x = constrain(mouseX, radius, width - radius);
            int next_y = constrain(mouseY, radius, height - radius);
            float diff_x = next_x - x;
            float diff_y = next_y - y;
            if (diff_y < 0)
            {
                factor_y = -1;
            }
            else
            {
                factor_y = 1;
            }
            if (diff_x < 0)
            {
                factor_x = -1;
            }
            else
            {
                factor_x = 1;
            }
            energy = constrain((diff_x)*(diff_x)+(diff_y)*(diff_y), 50, 200);
            ratio = abs(diff_y/diff_x);
        }

        void show()
        {
            fill(255);
            noStroke();
            ellipse(x, y, radius*2, radius*2);
        }
    }
}

class WormSketch extends Sketch_SensorEnabled
{
    WormSketch(boolean sensor_enabled)
    {
        super(sensor_enabled);
    }

    private int crawlSpeed, fallSpeed, wormSize;
    private Worm worm;
    private Laser laser;

    public void setup(){
        worm = new Worm("worm_", ".png", 4, "worm_fall.png", crawlSpeed, fallSpeed, wormSize);
        laser = new Laser(wormSize);
        frameRate(10);
    }

    public void draw(){
        background(0);
        laser.setPosition((int)map(constrain(tilt, -PI/8, PI/8), -PI/8, PI/8, 0, width)
                         , (int)map(constrain(pitch, -PI/4, -PI/8), -PI/4, -PI/8, height, 0));
        if (worm.loc != 4)
        {
            laser.show();
        }
        worm.show();
    }

    public void mousePressed()
    {
        worm.crawl(laser.x, laser.y);
    }

    @Override
    public void loadPrefs(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        crawlSpeed = Integer.valueOf(prefs.getString("crawlSpeed", "10"));
        crawlSpeed = constrain(crawlSpeed, 1, 100);
        fallSpeed = Integer.valueOf(prefs.getString("fallSpeed", "5"));
        fallSpeed = constrain(fallSpeed, 1, 100);
        wormSize = Integer.valueOf(prefs.getString("wormSize", "50"));
        wormSize = constrain(wormSize, 50, 400);
        setup();
    }

    class Worm
    {
        private PImage[] images;
        private int[] gif_widths;
        private int imageCount, gif_height, fall_index = 2, crawlSpeed, fallSpeed;
        private int frame, loc;
        private float x, y, next_x, next_y, rotation = 0;
        private boolean progress = false, reached = false;

        Worm(String imagePrefix, String extension, int count, String eatImage, int icrawlSpeed, int ifallSpeed, int wormSize)
        {
            imageCount = count;
            gif_height = wormSize;
            images = new PImage[imageCount + 1];
            gif_widths = new int[imageCount + 1];
            for (int i = 0; i < imageCount; i++) {
                String filename = imagePrefix + nf (i, 4) + extension;
                images[i] = loadImage(filename);
                images[i].resize(0 , gif_height);
                gif_widths[i] = images[i].width;
            }
            images[imageCount] = loadImage(eatImage);
            images[imageCount].resize(0 , gif_height);
            gif_widths[imageCount] = images[imageCount].width;
            crawlSpeed = icrawlSpeed;
            fallSpeed = ifallSpeed;

            loc = 0;
            x = width/2;
            next_x = x;
            y = height - gif_height;
            next_y = y;
        }

        void crawl(int lx, int ly)
        {
            if (progress)
            {
                if (((loc == 1 || loc == 3) && abs(mouseX - x) < gif_widths[imageCount] && abs(mouseY - y) < gif_height)
                    || ((loc == 2 || loc == 4) && abs(mouseX - x) < gif_height && abs(mouseY - y) < gif_widths[imageCount]))
                {
                    next_x = x;
                    next_y = height - gif_height;
                    loc = 5;
                    reached = false;
                }
            }
            else
            {
                next_x = constrain(lx, 0, width - gif_height);
                next_y = constrain(ly, 0, height - gif_widths[imageCount]);
                loc = 1;
                progress = true;
            }
        }

        void show()
        {
            if (loc < 4)
            {
                //crawling
                push();
                translate(x, y);
                rotate(rotation);
                image(images[frame], 0, 0);
                pop();
            }
            else if (rotation != 0 && !reached)
            {
                //fall
                push();
                translate(x, y);
                rotate(PI * 3 / 2);
                image(images[fall_index], 0, 0);
                pop();
            }
            if (loc > 0)
            {
                if(loc == 4)
                {
                    y += fallSpeed;
                    if (y > next_y)
                    {
                        y = next_y + gif_widths[fall_index];
                        push();
                        translate(x, y);
                        rotate(-PI/2);
                        image(images[imageCount], 0, 0);
                        pop();
                        reached = true;
                    }
                    return;
                }
                else if(loc == 5)
                {
                    y += fallSpeed;
                    if (y > next_y)
                    {
                        y = next_y;
                        loc = 0;
                        rotation = 0;
                        progress = false;
                    }
                    return;
                }
                frame++;
                if (frame == imageCount - 1)
                {
                    frame = 0;
                }
                switch(loc)
                {
                    case 1:
                        x -= crawlSpeed;
                        if (x < gif_height)
                        {
                            x = gif_height;
                            loc = 2;
                            rotation += PI/2;
                        }
                        break;
                    case 2:
                        y -= crawlSpeed;
                        if (y < gif_height)
                        {
                            y = gif_height;
                            loc = 3;
                            rotation += PI/2;
                        }
                        break;
                    case 3:
                        x += crawlSpeed;
                        if (x > next_x)
                        {
                            x = next_x;
                            loc = 4;
                            rotation += PI/2;
                        }
                        break;
                }
            }
        }
    }
    class Laser
    {
        int x, y, wormSize;
        Laser(int iwormSize)
        {
            wormSize = iwormSize;
        }
        void show()
        {
            fill(255);
            ellipse(x + 18 * wormSize/50, y, 10, 10);
            ellipse(x + 2 * wormSize/50, y + 18 * wormSize/50, 10, 10);
        }
        void setPosition(int ix, int iy)
        {
            x = ix;
            y = iy;
        }
    }
}

class GeckoSketch extends Sketch_SensorEnabled
{
    GeckoSketch(boolean sensor_enabled)
    {
        super(sensor_enabled);
    }

    private int crawlSpeed, fallSpeed, geckoSize;
    private Gecko gecko;
    private Laser laser;

    public void setup(){
        gecko = new Gecko("gecko_", ".png", 16, "gecko_fall.png", crawlSpeed, fallSpeed, geckoSize);
        laser = new Laser(geckoSize);
    }

    public void draw(){
        background(0);
        laser.setPosition((int)map(constrain(tilt, -PI/8, PI/8), -PI/8, PI/8, 0, width)
                , (int)map(constrain(pitch, -PI/4, -PI/8), -PI/4, -PI/8, height, 0));
        if (gecko.loc != 4)
        {
            laser.show();
        }
        gecko.show();
    }

    public void mousePressed()
    {
        gecko.crawl(laser.x, laser.y);
    }


    @Override
    public void loadPrefs(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        crawlSpeed = Integer.valueOf(prefs.getString("crawlSpeed", "10"));
        crawlSpeed = constrain(crawlSpeed, 1, 100);
        fallSpeed = Integer.valueOf(prefs.getString("fallSpeed", "5"));
        fallSpeed = constrain(fallSpeed, 1, 100);
        geckoSize = Integer.valueOf(prefs.getString("geckoSize", "50"));
        geckoSize = constrain(geckoSize, 50, 400);
        setup();
    }

    class Gecko
    {
        private PImage[] images;
        private int[] gif_widths;
        private int imageCount, gif_height, fall_index = 2, crawlSpeed, fallSpeed;
        private int frame, loc;
        private float x, y, next_x, next_y, rotation = 0;
        private boolean progress = false, reached = false;

        Gecko(String imagePrefix, String extension, int count, String eatImage, int icrawlSpeed, int ifallSpeed, int geckoSize)
        {
            imageCount = count;
            gif_height = geckoSize;
            images = new PImage[imageCount + 1];
            gif_widths = new int[imageCount + 1];
            for (int i = 0; i < imageCount; i++) {
                String filename = imagePrefix + nf (i, 4) + extension;
                images[i] = loadImage(filename);
                images[i].resize(0 , gif_height);
                gif_widths[i] = images[i].width;
            }
            images[imageCount] = loadImage(eatImage);
            images[imageCount].resize(0 , gif_height);
            gif_widths[imageCount] = images[imageCount].width;
            crawlSpeed = icrawlSpeed;
            fallSpeed = ifallSpeed;

            loc = 0;
            x = width/2;
            next_x = x;
            y = height - gif_height;
            next_y = y;
        }

        void crawl(int lx, int ly)
        {
            if (progress)
            {
                if (((loc == 1 || loc == 3) && abs(mouseX - x) < gif_widths[imageCount] && abs(mouseY - y) < gif_height)
                        || ((loc == 2 || loc == 4) && abs(mouseX - x) < gif_height && abs(mouseY - y) < gif_widths[imageCount]))
                {
                    next_x = x;
                    next_y = height - gif_height;
                    loc = 5;
                    reached = false;
                }
            }
            else
            {
                next_x = constrain(lx, gif_height, width);
                next_y = constrain(ly, 0, height - gif_widths[fall_index]);
                loc = 1;
                progress = true;
            }
        }

        void show()
        {
            if (loc < 4)
            {
                //crawling
                push();
                translate(x, y);
                rotate(rotation);
                if (loc == 0)
                {
                    image(images[frame], -gif_widths[0], 0);
                }
                else
                {
                    image(images[frame], 0, 0);
                }
                pop();
            }
            else if (rotation != 0 && !reached)
            {
                //fall
                push();
                translate(x, y);
                rotate(PI/2);
                image(images[fall_index], 0, 0);
                pop();
            }
            if (loc > 0)
            {
                if(loc == 4)
                {
                    y += fallSpeed;
                    if (y > next_y)
                    {
                        y = constrain(next_y + gif_widths[fall_index], 0, height - gif_widths[fall_index]);
                        translate(next_x - geckoSize * 0.54F, next_y);
                        rotate(constrain(tilt, -PI/4, PI/4));
                        push();
                        translate(x - next_x + geckoSize * 0.54F, y - next_y);
                        rotate(PI/2);
                        image(images[imageCount], 0, 0);
                        pop();
                        stroke(196, 115, 129);
                        strokeWeight(geckoSize/10);
                        line(0, 0, 0, constrain(1.8F * geckoSize, 0, height - gif_widths[fall_index] + 0.3F * geckoSize - next_y));
                        reached = true;
                    }
                    return;
                }
                else if(loc == 5)
                {
                    y += fallSpeed;
                    if (y > next_y)
                    {
                        y = next_y;
                        loc = 0;
                        rotation = 0;
                        progress = false;
                    }
                    return;
                }
                frame++;
                if (frame == imageCount - 1)
                {
                    frame = 0;
                }
                switch(loc)
                {
                    case 1:
                        x += crawlSpeed;
                        if (x > width - gif_height)
                        {
                            x = width - gif_height;
                            loc = 2;
                            rotation -= PI/2;
                        }
                        break;
                    case 2:
                        y -= crawlSpeed;
                        if (y < gif_height)
                        {
                            y = gif_height;
                            loc = 3;
                            rotation -= PI/2;
                        }
                        break;
                    case 3:
                        x -= crawlSpeed;
                        if (x < next_x)
                        {
                            x = next_x;
                            loc = 4;
                            rotation -= PI/2;
                        }
                        break;
                }
            }
        }
    }
    class Laser
    {
        int x, y, geckoSize;
        Laser(int igeckoSize)
        {
            geckoSize = igeckoSize;
        }
        void show()
        {
            fill(255);
            noStroke();
            ellipse(x - 27 * geckoSize/50, y, 30, 30);
        }
        void setPosition(int ix, int iy)
        {
            x = ix;
            y = iy;
        }
    }
}

class PuffSketch extends Sketch_SensorEnabled {
    PuffSketch(boolean sensor_enabled) {
        super(sensor_enabled);
    }

    private int puffSize, sizeIncrement, maxSize, steps;
    private Puffy puffy;
    public void setup(){
        puffy = new Puffy(puffSize, sizeIncrement, maxSize, steps, "puffy_body.png", "puffy_face.png");
        frameRate(20);
    }

    public void draw()
    {
        background(0);
        puffy.show();
    }

    public void mousePressed()
    {
        if(abs(mouseX - puffy.x) < puffy.size && abs(mouseY - puffy.y) < puffy.size)
        {
            puffy.sizeUp();
        }
        else
        {
            puffy.move(mouseX, mouseY);
        }
    }

    @Override
    public void loadPrefs() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        puffSize = Integer.valueOf(prefs.getString("puffSize", "200"));
        puffSize = constrain(puffSize, 50, 300);
        sizeIncrement = Integer.valueOf(prefs.getString("sizeIncrement", "50"));
        sizeIncrement = constrain(sizeIncrement, 10, 100);
        maxSize = Integer.valueOf(prefs.getString("maxSize", "800"));
        maxSize = constrain(maxSize, puffSize, 1000);
        steps = Integer.valueOf(prefs.getString("steps", "200"));
        steps = constrain(steps, 20, 500);
        setup();
    }

    class Puffy
    {
        private int x, y, oriSize, steps, numPoints, currPoint
                , sizeIncrement, maxSize, divideRatio = 20;
        private int trailSize = 20;
        private int[] cx, cy;
        private float sizeStep, size, angle;
        private float[] xcoor, ycoor;
        private boolean bigger = true;
        private PImage puffyBody, puffyFace;

        Puffy(int isize, int isizeIncrement, int imaxSize, int isteps, String body_filename, String face_filename)
        {
            x = width/2;
            y = height/2;
            size = isize;
            oriSize = isize;
            sizeIncrement = isizeIncrement;
            maxSize = imaxSize;
            steps = isteps;
            puffyBody = loadImage(body_filename);
            puffyFace = loadImage(face_filename);
            xcoor = new float[steps];
            ycoor = new float[steps];
            cx = new int[trailSize];
            cy = new int[trailSize];
            noStroke();
        }
        void show()
        {
            if (numPoints > 0)
            {
                x = (int)xcoor[currPoint];
                y = (int)ycoor[currPoint];
                if (currPoint > 0)
                {
                    if (x - (int)xcoor[currPoint - 1] == 0)
                    {
                        angle = 0;
                    }
                    else
                    {
                        angle = atan((float)(y - (int)ycoor[currPoint - 1])/(x - (int)xcoor[currPoint - 1])) - PI/4;
                    }
                }
                else
                {
                    angle = 0;
                }
                int endIndex = currPoint % trailSize;
                cx[endIndex] = x - (int)size/2;
                cy[endIndex] = y - (int)size/2;
                int red = (int)random(0, 255);
                int green = (int)random(0, 255);
                int blue = (int)random(0, 255);
                stroke(red, green, blue);
                strokeWeight(size);
                for (int i = max(0, endIndex + 1); i < min(trailSize, currPoint) - 1; i++)
                {
                    stroke(red, green, blue, i/trailSize * 255);
                    line(cx[i], cy[i], cx[i + 1], cy[i + 1]);
                }
                if (currPoint == trailSize)
                {
                    stroke(red, green, blue, currPoint/trailSize * 255);
                    line(cx[trailSize - 1], cy[trailSize - 1], cx[0], cy[0]);
                }
                for (int i = 0; i < endIndex; i++)
                {
                    stroke(red, green, blue, (currPoint + i + 1)/trailSize * 255);
                    line(cx[i], cy[i], cx[i + 1], cy[i + 1]);
                }
                currPoint++;
                size -= sizeStep;
                if (currPoint == numPoints)
                {
                    numPoints = 0;
                    bigger = true;
                }
            }

            push();
            if (bigger)
            {
                translate(x, y);
            }
            else
            {
                translate(x - (int)size/2, y - (int)size/2);
            }
            rotate(angle);
            imageMode(CENTER);
            image(puffyBody, 0, 0, (int)size, (int)size);
            image(puffyFace, 0, 0, oriSize, oriSize);
            pop();
        }
        void blowAway()
        {
            numPoints = 0;
            int prev_x4 = x;
            int prev_y4 = y;

            int x1 = prev_x4;
            int x2 = (int)random(0, width);
            int x3 = (int)random(0, width);
            int x4 = (int)random(0, width);
            int y1 = prev_y4;
            int y2 = (int)random(0, height);
            int y3 = (int)random(0, height);
            int y4 = (int)random(0, height);
            for (int ii = 0; ii < steps/divideRatio; ii++) {
                float t = ii / (float)(steps/divideRatio);
                xcoor[numPoints] = bezierPoint(x1, x2, x3, x4, t);
                ycoor[numPoints++] = bezierPoint(y1, y2, y3, y4, t);
            }
            prev_x4 = x4;
            prev_y4 = y4;
            int prev_x3, prev_y3;
            if (x4 == x3)
            {
                prev_x3 = x3;
                if (y3 < y4)
                {
                    prev_y3 = (int)random(y4, height);
                }
                else
                {
                    prev_y3 = (int)random(0, y4);
                }
            }
            else if (x3 < x4)
            {
                prev_x3 = (int)random(x4, width);
                prev_y3 = (y4 - y3)/(x4 - x3) * (x4 - prev_x3) + y4;
            }
            else
            {
                prev_x3 = (int)random(0, x4);
                prev_y3 = (y4 - y3)/(x4 - x3) * (x4 - prev_x3) + y4;
            }

            boolean through = true;
            while(through)
            {
                x1 = prev_x4;
                x2 = prev_x3;
                x3 = (int)random(0, width);
                x4 = (int)random(0, width);
                y1 = prev_y4;
                y2 = prev_y3;
                y3 = (int)random(0, height);
                y4 = (int)random(0, height);
                for (int ii = 0; ii < steps/divideRatio; ii++) {
                    float t = ii / (float)(steps/divideRatio);
                    xcoor[numPoints] = bezierPoint(x1, x2, x3, x4, t);
                    ycoor[numPoints++] = bezierPoint(y1, y2, y3, y4, t);
                    if (numPoints == steps)
                    {
                        through = false;
                        break;
                    }
                }
                prev_x4 = x4;
                prev_y4 = y4;
                if (x4 == x3)
                {
                    prev_x3 = x3;
                    if (y3 < y4)
                    {
                        prev_y3 = (int)random(y4, height);
                    }
                    else
                    {
                        prev_y3 = (int)random(0, y4);
                    }
                }
                else if (x3 < x4)
                {
                    prev_x3 = (int)random(x4, width);
                    prev_y3 = (y4 - y3)/(x4 - x3) * (x4 - prev_x3) + y4;
                }
                else
                {
                    prev_x3 = (int)random(0, x4);
                    prev_y3 = (y4 - y3)/(x4 - x3) * (x4 - prev_x3) + y4;
                }
            }
            currPoint = 0;
            sizeStep = (size - oriSize)/numPoints;
            fill(255);
        }
        void sizeUp()
        {
            if (bigger)
            {
                size += sizeIncrement;
                if (size > maxSize)
                {
                    bigger = false;
                    blowAway();
                }
            }
        }
        void move(int ix, int iy)
        {
            if (bigger){
                x = ix;
                y = iy;
            }
        }
    }
}
class BubbleTeaSketch extends Sketch_SensorEnabled {
    BubbleTeaSketch(boolean sensor_enabled) {
        super(sensor_enabled);
    }

    private int drinkSize = 100, charSize = 50, handSize = 30, maxSize = 300;
    private boolean healthy = false;
    private Drink drink;
    private Bubbletea bubbletea;
    private Water water;
    private Person person;
    public void setup() {
        bubbletea = new Bubbletea(drinkSize, "bubbletea.png", width/2, height/4, 0.05F, height/2 + charSize/2);
        water = new Water(drinkSize, "water.png", width/2, height/4, 0.05F, height/2 + charSize/2);
        drink = bubbletea;
        person = new Person(charSize, handSize, "boy.png", "hand.png", width/2 + drinkSize/2, height/2, maxSize);
    }

    public void draw() {
        background(255, 128, 0);
        drink.show();
        person.grow(drink.getAmount());
        person.show();
    }

    public void mousePressed() {
        if(!healthy)
        {
            drink = water;
            healthy = true;
        }
        else
        {
            drink = bubbletea;
            healthy = false;
        }
    }

    @Override
    public void loadPrefs() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        drinkSize = Integer.valueOf(prefs.getString("drinkSize", "100"));
        drinkSize = constrain(drinkSize, 30, 500);
        charSize = Integer.valueOf(prefs.getString("charSize", "100"));
        charSize = constrain(charSize, 30, 200);
        handSize = Integer.valueOf(prefs.getString("handSize", "30"));
        handSize = constrain(handSize, 30, 200);
        maxSize = Integer.valueOf(prefs.getString("maxSize", "300"));
        maxSize = constrain(maxSize, charSize, 800);
        setup();
    }

    class Drink
    {
        private int size, x, y;
        int thickness;
        private float capWidth, endY;
        private PImage drinkImage;
        Drink(int isize, String filename, int ix, int iy, float icapWidth, float iendY)
        {
            size = isize;
            drinkImage = loadImage(filename);
            drinkImage.resize(size, 0);
            x = ix;
            y = iy;
            capWidth = icapWidth;
            endY = iendY;
        }

        void show()
        {
            float angle = map(constrain(tilt, -PI/8, PI/8), -PI/8, PI/8, PI/5, PI/2);
            push();
            translate(x + size/2, y);
            if (angle > PI/4)
            {
                setColor();
                thickness = (int)map(angle, PI/4, PI/2, 1, size/ 10);
                strokeWeight(thickness);
                float cx = capWidth * size * cos(angle) + thickness/2F;
                float cy = capWidth * size * sin(angle);
                line(cx, cy, cx, endY - y);
                noStroke();
                arc(capWidth * size * cos(angle), capWidth * size * sin(angle), thickness * 2, thickness * 2, PI + angle, 2*PI, PIE);
            }
            else
            {
                thickness = 0;
            }
            rotate(angle);
            image(drinkImage, -drinkImage.width/2, 0);
            pop();
        }
        void setColor(){}

        int getAmount()
        {
            return thickness;
        }
    }

    class Bubbletea extends Drink
    {
        Bubbletea(int isize, String filename, int ix, int iy, float icapWidth, float iendY)
        {
            super(isize, filename, ix, iy, icapWidth, iendY);
        }
        void setColor()
        {
            stroke(196, 153, 108);
            fill(196, 153, 108);
        }
        int getAmount()
        {
            return thickness;
        }
    }

    class Water extends Drink
    {
        Water(int isize, String filename, int ix, int iy, float icapWidth, float iendY)
        {
            super(isize, filename, ix, iy, icapWidth, iendY);
        }
        void setColor()
        {
            stroke(156, 211, 219);
            fill(156, 211, 219);
        }
        int getAmount()
        {
            return -thickness;
        }
    }

    class Person
    {
        private int x, y, bodySize, maxSize, oriBodySize;
        private PImage faceImage, handImage;
        Person(int icharSize, int ihandSize, String faceFilename, String handFilename, int ix, int iy, int imaxSize)
        {
            bodySize = (int)(icharSize * 0.5);
            oriBodySize = bodySize;
            maxSize = imaxSize;
            faceImage = loadImage(faceFilename);
            faceImage.resize(0, icharSize);
            handImage = loadImage(handFilename);
            handImage.resize(ihandSize, 0);
            x = ix;
            y = iy;
        }
        void show()
        {
            fill(255, 0, 0);
            noStroke();
            float cx = x + faceImage.width * 0.05F;
            float cy = y + faceImage.height * 0.8F + bodySize/2F;
            stroke(34, 67, 107);
            ellipse(cx, cy, bodySize, bodySize);
            push();
            translate(cx + bodySize/2F * cos(PI), cy + bodySize/2F * sin(PI));
            rotate(0);
            scale(-1.0F, 1.0F);
            image(handImage, -handImage.width * 3/4, 0);
            pop();
            push();
            translate(cx - bodySize/2F * cos(PI), cy + bodySize/2F * sin(PI));
            rotate(0);
            image(handImage, -handImage.width * 3/4, 0);
            pop();
            image(faceImage, x - faceImage.width * 0.2F, y);
        }
        void grow(int amount)
        {
            bodySize += amount/4;
            if (bodySize > maxSize)
            {
                bodySize = maxSize;
            }
            else if (bodySize < oriBodySize)
            {
                bodySize = oriBodySize;
            }
        }
    }
}

class TentacleSketch extends Sketch_SensorEnabled {
    TentacleSketch(boolean sensor_enabled) {
        super(sensor_enabled);
    }

    private int numSegments;
    private float[] x;
    private float[] y;
    private float[] angle;
    private float segLength;
    private float targetX, targetY;

    public void setup() {
        strokeWeight(20.0F);
        stroke(255, 100);
        x = new float[numSegments];
        y = new float[numSegments];
        angle = new float[numSegments];
        x[numSegments-1] = width/2;
        y[numSegments-1] = height;
    }

    public void draw() {
        background(0);
        reachSegment(0, mouseX, mouseY);
        for(int i=1; i<numSegments; i++) {
            reachSegment(i, targetX, targetY);
        }
        for(int i=x.length-1; i>=1; i--) {
            positionSegment(i, i-1);
        }
        for(int i=0; i<x.length; i++) {
            segment(x[i], y[i], angle[i], (i+1)*2);
        }
    }

    private void positionSegment(int a, int b) {
        x[b] = x[a] + cos(angle[a]) * segLength;
        y[b] = y[a] + sin(angle[a]) * segLength;
    }

    private void reachSegment(int i, float xin, float yin) {
        float dx = xin - x[i];
        float dy = yin - y[i];
        angle[i] = atan2(dy, dx);
        targetX = xin - cos(angle[i]) * segLength;
        targetY = yin - sin(angle[i]) * segLength;
    }

    private void segment(float x, float y, float a, float sw) {
        strokeWeight(sw);
        pushMatrix();
        translate(x, y);
        rotate(a);
        line(0, 0, segLength, 0);
        popMatrix();
    }
    @Override
    public void loadPrefs() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        numSegments = Integer.valueOf(prefs.getString("numSegments", "30"));
        numSegments = constrain(numSegments, 2, 50);
        segLength = Integer.valueOf(prefs.getString("segLength", "50"));
        segLength = constrain(segLength, 30, 200);
        setup();
    }
}

class BlueGrowthSketch extends Sketch_SensorEnabled {
    BlueGrowthSketch(boolean sensor_enabled) {
        super(sensor_enabled);
    }

    private ArrayList<Particle> pts;
    private boolean onPressed;
    private int minWeight, maxWeight;

    public void setup() {
        smooth();
        frameRate(30);
        colorMode(RGB);
        rectMode(CENTER);
        pts = new ArrayList<>();
        background(0);
    }

    public void draw() {
        if (onPressed) {
            for (int i = 0; i < 10; i++) {
                Particle newP = new Particle(mouseX, mouseY, i, i, minWeight, maxWeight);
                pts.add(newP);
            }
        }
        for (int i = 0; i < pts.size(); i++) {
            Particle p = pts.get(i);
            p.update();
            p.display();
        }
        for (int i = pts.size()-1; i > -1; i--) {
            Particle p = pts.get(i);
            if (p.dead) {
                pts.remove(i);
            }
        }
    }

    public void mousePressed() {
        onPressed = true;
    }

    public void mouseReleased() {
        onPressed = false;
    }

    @Override
    public void loadPrefs() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        minWeight = Integer.valueOf(prefs.getString("minWeight", "10"));
        minWeight = constrain(minWeight, 3, 100);
        maxWeight = Integer.valueOf(prefs.getString("maxWeight", "100"));
        maxWeight = constrain(maxWeight, minWeight, 300);
    }
    class Particle{
        private PVector loc, vel, acc;
        private int lifeSpan, passedLife;
        private boolean dead;
        private float alpha, weight, weightRange, xOffset, yOffset;
        private int c;

        Particle(float x, float y, float ixOffset, float iyOffset, int minWeight, int maxWeight){
            loc = new PVector(x,y);
            float randDegrees = random(360);
            vel = new PVector(cos(radians(randDegrees)), sin(radians(randDegrees)));
            vel.mult(random(5));

            acc = new PVector(0,0);
            lifeSpan = (int)(random(30, 90));
            //decay = random(0.75, 0.9);
            c = color(random(255), random(255), 255);
            weightRange = random(minWeight, maxWeight);
            xOffset = ixOffset;
            yOffset = iyOffset;
        }

        void update(){
            if(passedLife >= lifeSpan)
            {
                dead = true;
            }
            else
            {
                passedLife++;
            }

            alpha = (float)(lifeSpan-passedLife)/lifeSpan * 70+50;
            weight = (float)(lifeSpan-passedLife)/lifeSpan * weightRange;
            acc.set(0,0);
            float rn = (noise((loc.x+frameCount+xOffset)* 0.01F, (loc.y+frameCount+yOffset)* 0.01F)-0.5F)*4*PI;
            //frameCount is to add randomness different frames (i.e. clicking at same point)
            //xOffset, yOffset is to scatter the same group of points
            //float mag = noise((loc.y+frameCount)*0.01, (loc.x+frameCount)*0.01);
            PVector dir = new PVector(cos(rn),sin(rn));
            acc.add(dir);
            //acc.mult(mag);

            float randDegrees = random(360);
            PVector randV = new PVector(cos(radians(randDegrees)), sin(radians(randDegrees)));
            //randV.mult(0.5);
            acc.add(randV);
            vel.add(acc);
            //vel.mult(decay);
            vel.limit(3);
            loc.add(vel);
        }

        void display(){
            //black border
            strokeWeight(weight+1.5F);
            stroke(0, alpha);
            point(loc.x, loc.y);

            strokeWeight(weight);
            stroke(c);
            point(loc.x, loc.y);
        }
    }
}