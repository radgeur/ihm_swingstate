package e201.skeleton ;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;

import javax.swing.JFrame;

import fr.lri.swingstates.canvas.CExtensionalTag;
import fr.lri.swingstates.canvas.CRectangle;
import fr.lri.swingstates.canvas.CShape;
import fr.lri.swingstates.canvas.CStateMachine;
import fr.lri.swingstates.canvas.CText;
import fr.lri.swingstates.canvas.Canvas;
import fr.lri.swingstates.canvas.transitions.EnterOnShape;
import fr.lri.swingstates.canvas.transitions.PressOnShape;
import fr.lri.swingstates.sm.State;
import fr.lri.swingstates.sm.Transition;

/**
 * @author Nicolas Roussel (roussel@lri.fr)
 *
 */
public class SimpleButton {

    private CText label ;
    private CRectangle rect;
    private CExtensionalTag overed;
    private CExtensionalTag clicked;

    SimpleButton(Canvas canvas, String text) {
	   label = canvas.newText(0, 0, text, new Font("verdana", Font.PLAIN, 12));
	   rect = canvas.newRectangle(0,0,50,15);
	   //put ever label above rect
	   label.above(rect);
	   //rect is now a child of label and when something happen to label, it is apply on rect too
	   label.addChild(rect);
	   
	   //create one tag per action
	   //when the mouse is over, the stroke are up
	   overed = new CExtensionalTag(canvas) {		   
		   public void added(CShape s) { 
		        s.setOutlined(true).setStroke(new BasicStroke(2));
		    }
		    public void removed(CShape s) {
		        s.setStroke(new BasicStroke(1));
		    }
	   };
	 
	   //when the button is clicked, the color inside become yellow
	   clicked = new CExtensionalTag(canvas) {
		   Paint initColor;
		   public void added(CShape s) { 
		        initColor = getRect().getFillPaint();
		        getRect().setFillPaint(Color.YELLOW);
		    }
		    public void removed(CShape s) {
		    	getRect().setFillPaint(initColor);
		    }
	   };
	   
	   //create a stateMachine with different states
	   CStateMachine sm = new CStateMachine(){
		   //when nothin happen
		   public State nothing = new State(){
			   Transition enterBox = new EnterOnShape(">> over") {};
		   };
		   
		   //when the mouse is over
		   public State over = new State(){
			   public void enter() {
				   rect.addTag(overed);
			   }
			   
			    Transition click = new PressOnShape(">> click") {};
			   
			   public void leave() {
				   rect.removeTag(overed);
			   }
		   };
		   
		   //when clicked
		  public State click = new State(){
			  public void enter(){
				  rect.addTag(clicked);
			  }
			  
			  public void leave(){
				  rect.removeTag(clicked);
			  }
		  };
		   
		   //when out of the shape
	   };
	   sm.attachTo(canvas);
    }

    public void action() {
	   System.out.println("ACTION!") ;
    }

    public CShape getShape() {
	   return label;
    }
    
    public CShape getRect() {
    	return rect;
    }

    static public void main(String[] args) {
	   JFrame frame = new JFrame() ;
	   Canvas canvas = new Canvas(400,400) ;
	   frame.getContentPane().add(canvas) ;
	   frame.pack() ;
	   frame.setVisible(true) ;

	   SimpleButton simple = new SimpleButton(canvas, "simple") ;
	   simple.getShape().translateBy(100,100) ;
    }

}
