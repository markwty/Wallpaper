package com.example.wallpaper;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;

import android.util.Log;

import java.util.ArrayList;

class Star extends PApplet
{
    private int start_size,max_size,partner,cof_increment,width,height;
    private float i,x,y,z,ori_z,ix,iy,sx,sy,sz,speed,max_speed;
    //private float px,py,pz;
    float increment;
    private PGraphics lay_Stars;
    private int col_Star;//color

    Star(PGraphics ilay_Stars,int istart_size,int imax_size,float imax_speed,int iwidth,int iheight)
    {
        lay_Stars=ilay_Stars;
        start_size=istart_size;
        width=iwidth;
        height=iheight;
        x=random(-width,width);
        y=random(-height,height);
        z=max(width,height);
        //pz=z;
        partner=-1;
        max_size=imax_size;
        max_speed=imax_speed;
        //color(255,215,0);
        col_Star=color(255);
    }

    void update(float ispeed)
    {
        if (speed<0)
        {
            ori_z=z;
        }
        speed=ispeed;
        z=z-speed;
        if (z<=0)
        {
            x=random(-width,width);
            y=random(-height,height);
            z=max(width,height);
            //pz=z;
        }
    }
    void show()
    {
        lay_Stars.beginDraw();
        lay_Stars.fill(col_Star);
        lay_Stars.noStroke();
        sx=map(x/z,-1,1,0,width);
        sy=map(y/z,-1,1,0,height);
        sz=map(start_size/z,(float)(start_size/max(width,height)),(float)(start_size*0.1),2,max_size);
        //start_size*0.1 where 0.1 is small but not too small to prevent division into infinity
        lay_Stars.beginShape();
        for (i=0;i<=TWO_PI;i+=2*PI/5)
        {
            float ssx=sx+cos(i)*sz;
            float ssy=sy+sin(i)*sz;
            lay_Stars.vertex(ssx,ssy);
            ssx=sx+cos(i+PI/10)*sz/2;
            ssy=sy+sin(i+PI/10)*sz/2;
            lay_Stars.vertex(ssx,ssy);
        }
        lay_Stars.endShape(CLOSE);
        lay_Stars.endDraw();
        //lay_Lines.beginDraw();
        //lay_Lines.stroke(20);
        //px=map(x/pz,-1,1,0,width);
        //py=map(y/pz,-1,1,0,height);
        //lay_Lines.line(px,py,(sx-px)*speed/max_speed+px,(sy-py)*speed/max_speed+py);
        //lay_Lines.endDraw();
    }

    void cluster(ArrayList<Integer> arr_Pixels)
    {
        increment=0;
        partner=arr_Pixels.get(floor(random(0,(float)(arr_Pixels.size()-0.1))));
    }

    void attract(int icof_increment)
    {
        cof_increment=icof_increment;
        sz=map(start_size/z,(float)(start_size/max(width,height)),(float)(start_size*0.1),2,max_size);
        ix=sx+(partner%width-sx)*increment/cof_increment;
        iy=sy+(floor((float)(partner/width))-sy)*increment/cof_increment;
        lay_Stars.beginDraw();
        lay_Stars.noStroke();
        lay_Stars.fill(col_Star);

        lay_Stars.beginShape();
        for (i=0;i<=TWO_PI;i=i+2*PI/5)
        {
            float ssx=ix+cos(i)*sz;
            float ssy=iy+sin(i)*sz;
            lay_Stars.vertex(ssx,ssy);
            ssx=ix+cos(i+PI/10)*sz/2;
            ssy=iy+sin(i+PI/10)*sz/2;
            lay_Stars.vertex(ssx,ssy);
        }
        lay_Stars.endShape(CLOSE);
        lay_Stars.endDraw();
        increment=increment+map(speed,0,-max_speed,0,1);
        increment=constrain(increment,0,cof_increment);
    }

    void unattract()
    {
        sz=map(start_size/z,(float)(start_size/max(width,height)),(float)(start_size*0.1),2,max_size);
        ix=sx+(partner%width-sx)*increment/cof_increment;
        iy=sy+(floor((float)partner/width)-sy)*increment/cof_increment;
        lay_Stars.beginDraw();
        lay_Stars.noStroke();
        lay_Stars.fill(col_Star);
        lay_Stars.beginShape();
        for (i=0;i<=TWO_PI;i=i+2*PI/5)
        {
            float ssx=ix+cos(i)*sz;
            float ssy=iy+sin(i)*sz;
            lay_Stars.vertex(ssx,ssy);
            ssx=ix+cos(i+PI/10)*sz/2;
            ssy=iy+sin(i+PI/10)*sz/2;
            lay_Stars.vertex(ssx,ssy);
        }
        lay_Stars.endShape(CLOSE);
        lay_Stars.endDraw();
        increment=increment-map(speed,0,max_speed,0,1);
        increment=constrain(increment,0,cof_increment);
        if (increment==0)
        {
            z=ori_z;
        }
    }
}

class block_Letter extends PApplet
{
    private PGraphics lay;
    private String word;
    private float y,gap;
    private int [] arr_Blocks=new int [20];
    private int width;
    //private ArrayList <Float> arr_Data=new ArrayList <>();

    block_Letter(String iword,float iystart,float igap,PGraphics ilay,int iwidth)
    {
        word=iword;
        gap=igap;
        y=iystart;
        lay=ilay;
        width = iwidth;
    }

    void show()
    {
        int i, x;
        for (x = 0; x < word.length(); x++)
        {
            switch (word.substring(x, x + 1))
            {
                case "a":
                    for (i = 0; i < 20; i++)
                    {
                        if (i == 1 || i == 2 || i == 4 || (i > 6 && i < 13) || i == 15 || i == 16 || i == 19)
                        {
                            arr_Blocks[i] = 1;
                        }
                        else
                        {
                            arr_Blocks[i] = 0;
                        }
                    }
                    break;
                case "b":
                    for (i = 0; i < 20; i++)
                    {
                        if ((i >= 0 && i < 3) || i == 4 || (i > 6 && i < 11) || i == 12 || (i > 14 && i < 19))
                        {
                            arr_Blocks[i] = 1;
                        }
                        else
                        {
                            arr_Blocks[i] = 0;
                        }
                    }
                    break;
                case "c":
                    for (i = 0; i < 20; i++)
                    {
                        if (i == 1 || i == 2 || i == 4 || i == 7 || i == 8 || i == 12 || i == 15 || i == 17 || i == 18)
                        {
                            arr_Blocks[i] = 1;
                        }
                        else
                        {
                            arr_Blocks[i] = 0;
                        }
                    }
                    break;
                case "d":
                    for (i = 0; i < 20; i++)
                    {
                        if ((i >= 0 && i < 3) || i == 4 || i == 7 || i == 8 || i == 11 || i == 12 || (i > 14 && i < 19))
                        {
                            arr_Blocks[i] = 1;
                        }
                        else
                        {
                            arr_Blocks[i] = 0;
                        }
                    }
                    break;
                case "e":
                    for (i = 0; i < 20; i++)
                    {
                        if ((i >= 0 && i < 5) || (i > 9 && i < 11) || i == 12 || i > 15)
                        {
                            arr_Blocks[i] = 1;
                        }
                        else
                        {
                            arr_Blocks[i] = 0;
                        }
                    }
                    break;
                case "f":
                    for (i = 0; i < 20; i++)
                    {
                        if ((i >= 0 && i < 5) || (i > 7 && i < 11) || i == 12 || (i == 16))
                        {
                            arr_Blocks[i] = 1;
                        }
                        else
                        {
                            arr_Blocks[i] = 0;
                        }
                    }
                    break;
                case "g":
                    for (i = 0; i < 20; i++)
                    {
                        if ((i > 0 && i < 5) || i == 8 || (i > 9 && i < 13) || i == 15 || i > 16)
                        {
                            arr_Blocks[i] = 1;
                        }
                        else
                        {
                            arr_Blocks[i] = 0;
                        }
                    }
                    break;
                case "h":
                    for (i = 0; i < 20; i++)
                    {
                        if (i == 0 || i == 3 || i == 4 || (i > 6 && i < 13) || i == 15 || i == 16 || i == 19)
                        {
                            arr_Blocks[i] = 1;
                        }
                        else
                        {
                            arr_Blocks[i] = 0;
                        }
                    }
                    break;
                case "i":
                    for (i = 0; i < 20; i++)
                    {
                        if ((i >= 0 && i < 3) || i == 5 || i == 9 || i == 13 || (i > 15 && i < 19))
                        {
                            arr_Blocks[i] = 1;
                        }
                        else
                        {
                            arr_Blocks[i] = 0;
                        }
                    }
                    break;
                case "j":
                    for (i = 0; i < 20; i++)
                    {
                        if ((i >= 0 && i < 4) || i == 7 || i == 11 || i == 12 || i == 15 || i == 17 || i == 18)
                        {
                            arr_Blocks[i] = 1;
                        }
                        else
                        {
                            arr_Blocks[i] = 0;
                        }
                    }
                    break;
                case "k":
                    for (i = 0; i < 20; i++)
                    {
                        if (i == 0 || i == 3 || i == 4 || i == 6 || i == 8 || i == 9 || i == 12 || i == 14 || i == 16 || i == 19)
                        {
                            arr_Blocks[i] = 1;
                        }
                        else
                        {
                            arr_Blocks[i] = 0;
                        }
                    }
                    break;
                case "l":
                    for (i = 0; i < 20; i++)
                    {
                        if (i == 0 || i == 4 || i == 8 || i == 12 || i > 15)
                        {
                            arr_Blocks[i] = 1;
                        }
                        else
                        {
                            arr_Blocks[i] = 0;
                        }
                    }
                    break;
                case "m":
                    for (i = 0; i < 20; i++)
                    {
                        if (i == 0 || (i > 2 && i < 9) || i == 11 || i == 12 || i == 15 || i == 16 || i == 19)
                        {
                            arr_Blocks[i] = 1;
                        }
                        else
                        {
                            arr_Blocks[i] = 0;
                        }
                    }
                    break;
                case "n":
                    for (i = 0; i < 20; i++)
                    {
                        if (i == 0 || i == 3 || i == 4 || i == 7 || i == 8 || i == 9 || i == 11 || i == 12 || i == 14 || i == 15 || i == 16 || i == 19)
                        {
                            arr_Blocks[i] = 1;
                        }
                        else
                        {
                            arr_Blocks[i] = 0;
                        }
                    }
                    break;
                case "o":
                    for (i = 0; i < 20; i++)
                    {
                        if (i == 1 || i == 2 || i == 4 || i == 7 || i == 8 || i == 11 || i == 12 || i == 15 || i == 17 || i == 18)
                        {
                            arr_Blocks[i] = 1;
                        }
                        else
                        {
                            arr_Blocks[i] = 0;
                        }
                    }
                    break;
                case "p":
                    for (i = 0; i < 20; i++)
                    {
                        if ((i >= 0 && i < 3) || i == 4 || (i > 6 && i < 11) || i == 12 || i == 16)
                        {
                            arr_Blocks[i] = 1;
                        }
                        else
                        {
                            arr_Blocks[i] = 0;
                        }
                    }
                    break;
                case "q":
                    for (i = 0; i < 20; i++)
                    {
                        if (i == 1 || i == 4 || i == 6 || i == 8 || i == 10 || i == 12 || i == 14 || i > 16)
                        {
                            arr_Blocks[i] = 1;
                        }
                        else
                        {
                            arr_Blocks[i] = 0;
                        }
                    }
                    break;
                case "r":
                    for (i = 0; i < 20; i++)
                    {
                        if ((i >= 0 && i < 3) || i == 4 || (i > 6 && i < 11) || i == 12 || i == 14 || i == 16 || i == 19)
                        {
                            arr_Blocks[i] = 1;
                        }
                        else
                        {
                            arr_Blocks[i] = 0;
                        }
                    }
                    break;
                case "s":
                    for (i = 0; i < 20; i++)
                    {
                        if ((i > 0 && i < 5) || i == 9 || i == 10 || (i > 14 && i < 19))
                        {
                            arr_Blocks[i] = 1;
                        }
                        else
                        {
                            arr_Blocks[i] = 0;
                        }
                    }
                    break;
                case "t":
                    for (i = 0; i < 20; i++)
                    {
                        if ((i >= 0 && i < 3) || i == 5 || i == 9 || i == 13 || i == 17)
                        {
                            arr_Blocks[i] = 1;
                        }
                        else
                        {
                            arr_Blocks[i] = 0;
                        }
                    }
                    break;
                case "u":
                    for (i = 0; i < 20; i++)
                    {
                        if (i == 0 || i == 3 || i == 4 || i == 7 || i == 8 || i == 11 || i == 12 || i > 14)
                        {
                            arr_Blocks[i] = 1;
                        }
                        else
                        {
                            arr_Blocks[i] = 0;
                        }
                    }
                    break;
                case "v":
                    for (i = 0; i < 20; i++)
                    {
                        if (i == 0 || i == 3 || i == 4 || i == 7 || i == 8 || i == 11 || i == 12 || i == 15 || i == 17 || i == 18)
                        {
                            arr_Blocks[i] = 1;
                        }
                        else
                        {
                            arr_Blocks[i] = 0;
                        }
                    }
                    break;
                case "w":
                    for (i = 0; i < 20; i++)
                    {
                        if (i == 0 || i == 3 || i == 4 || i == 7 || i == 8 || (i > 10 && i < 17) || i == 19)
                        {
                            arr_Blocks[i] = 1;
                        }
                        else
                        {
                            arr_Blocks[i] = 0;
                        }
                    }
                    break;
                case "x":
                    for (i = 0; i < 20; i++)
                    {
                        if (i == 0 || i == 3 || i == 4 || i == 7 || i == 9 || i == 10 || i == 12 || i == 15 || i == 16 || i == 19)
                        {
                            arr_Blocks[i] = 1;
                        }
                        else
                        {
                            arr_Blocks[i] = 0;
                        }
                    }
                    break;
                case "y":
                    for (i = 0; i < 20; i++)
                    {
                        if (i == 0 || i == 3 || i == 4 || i == 7 || (i > 8 && i < 12) || i == 15 || i == 17 || i == 18)
                        {
                            arr_Blocks[i] = 1;
                        }
                        else
                        {
                            arr_Blocks[i] = 0;
                        }
                    }
                    break;
                case "z":
                    for (i = 0; i < 20; i++)
                    {
                        if ((i >= 0 && i < 5) || i == 7 || i == 9 || i == 10 || i == 12 || i > 15)
                        {
                            arr_Blocks[i] = 1;
                        }
                        else
                        {
                            arr_Blocks[i] = 0;
                        }
                    }
                    break;
                case " ":
                    for (i = 0; i < 20; i++)
                    {
                        arr_Blocks[i] = 0;
                    }
                    break;
            }
            lay.beginDraw();
            lay.noStroke();
            lay.fill(255, 0, 0);
            for (i = 0; i < 20; i++) {
                if (arr_Blocks[i] == 1) {
                    lay.rect((width - gap * word.length()) / 2 + gap * x + i % 4 * gap / 5, y + floor((float) (i / 4)) * gap / 4, gap / 5, gap / 4);
                    //arr_Data.add((width-gap*word.length())/2+gap*x+i%4*gap/5+gap/10);
                    //arr_Data.add(y+floor(i/4)*gap/4+gap/8);
                }
            }
            lay.endDraw();
        }
    }
}