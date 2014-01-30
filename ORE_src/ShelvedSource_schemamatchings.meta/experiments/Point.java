package schemamatchings.meta.experiments;

import java.io.Serializable;

public class Point implements Serializable{

  public double x;
  public double y;
  public String sx;
  public String sy;

  public Point(){}

  public Point(double x,double y) {
    this.x = x;
    this.y = y;
  }

  public Point(String sx,String sy){
    this.sx = sx;
    this.sy = sy;
  }

  public double getX(){
    return x;
  }

  public void setX(double x){
    this.x = x;
  }

  public double getY(){
    return y;
  }

  public void setY(double y){
    this.y = y;
  }

  public String getSx(){
   return sx;
 }

 public void setSx(String sx){
   this.sx = sx;
 }

 public String getSy(){
   return sy;
 }

 public void setSy(String sy){
   this.sy = sy;
  }
}