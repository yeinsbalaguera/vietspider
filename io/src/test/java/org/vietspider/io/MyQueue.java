package org.vietspider.io;
/*Lap chuong trinh cho bai toan sau:
       Nhap tu ban phim so nguyen n
           +Neu n le thi nhap n so nguyen vao ngan xep va tinh tong cua m so do
           +Neu n chan thi nhap n so nguyen vao hang doi va tinh tong cua m so do*/
public class MyQueue   {// khuyen cao  dat ten file java trung voi ten class de la Myqueue
  //ten class nen de la MyQueue
  private int last =-1;
  private int cursor = -1;
  private int queue[];

  public MyQueue(int size) {
    this.queue=new int[size];
    last = -1;
    cursor = -1;
  }
  public int getSize() {
    return queue.length;
  }

  //push voi kieu tra ve la void
  public void push(int value) { 
    if(last < queue.length-1) {
      last++;
      queue[last]=value;
      return;
    }
    throw new RuntimeException("Hang doi da day");     
  }
  
  public boolean hasNext() { return cursor < last; }
  
  public void moveFirst() { cursor = -1; }

  public int pop() {
    if(cursor < last) {
      cursor++;
      return queue[cursor];
    }
    throw new RuntimeException("Hang doi rong");
  }        

}
 
