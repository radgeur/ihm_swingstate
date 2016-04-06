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
import fr.lri.swingstates.canvas.transitions.EnterOnTag;
import fr.lri.swingstates.canvas.transitions.LeaveOnTag;
import fr.lri.swingstates.canvas.transitions.PressOnTag;
import fr.lri.swingstates.canvas.transitions.ReleaseOnTag;
import fr.lri.swingstates.debug.StateMachineVisualization;
import fr.lri.swingstates.sm.State;
import fr.lri.swingstates.sm.Transition;
import fr.lri.swingstates.sm.transitions.Press;
import fr.lri.swingstates.sm.transitions.Release;
import fr.lri.swingstates.sm.transitions.TimeOut;

/**
 * @author Nicolas Roussel (roussel@lri.fr)
 *
 */
public class SimpleButton {

    private CText label ;
    private CRectangle rect;
    private CExtensionalTag overed, clicked, container;
    private CStateMachine sm;
    private Canvas canvas;
    private final int button, modifier, timer;
    private int nbPress;
    
    SimpleButton(Canvas canvas, String text) {
    	this.canvas = canvas;
    	label = canvas.newText(0, 0, text, new Font("verdana", Font.PLAIN, 12));
    	rect = canvas.newRectangle(0,0,50,15);
    	this.button = 1;
    	this.timer = 800;
    	this.modifier = 0;
    	this.nbPress = 0;
    	construct();
    }
    
    SimpleButton(Canvas canvas, String text, int button, int modifier) {
    	this.canvas = canvas;
 	   	label = canvas.newText(0, 0, text, new Font("verdana", Font.PLAIN, 12));
 	   	rect = canvas.newRectangle(0,0,50,15);
 	   	this.button = button;
 	   	this.timer = 800;
 	   	this.modifier = modifier;
 	   	this.nbPress = 0;
 	   	construct();
    }
    
    
    public void construct(){
	   //put ever label above rect
	   label.above(rect);
	   //rect is now a child of label and when something happen to label, it is apply on rect too
	   label.addChild(rect);
	   
	   //create one tag per action
	   container = new CExtensionalTag(canvas){};
	   rect.addTag(container);
	   label.addTag(container);
	   
	   
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
	   sm = new CStateMachine(){
		   //when nothing happen
		   public State nothing = new State(){
			   Transition enterBox = new EnterOnTag(container, ">> over") {};
		   };
		   
		   //when the mouse is over
		   public State over = new State(){
			   public void enter() {
				   rect.addTag(overed);
			   }
			   
			   Transition click = new PressOnTag(container, button, modifier, ">> click") {
				   public void action(){
	    				nbPress++;
						armTimer(timer, false);
					}
			   };
			   Transition leave = new LeaveOnTag(container, ">> nothing") {};
			   
			   public void leave() {
				   rect.removeTag(overed);
			   }
		   };
		   
		   //when clicked
		  public State click = new State(){
			  public void enter(){
				  rect.addTag(clicked);
			  }
			  
			  Transition releaseOnShape = new ReleaseOnTag(container, button, ">>over"){};
			  
			  Transition leaveOnTag = new LeaveOnTag(container, ">>out"){};
			  
			  public void leave(){
				  rect.removeTag(clicked);
			  }
			  
			  Transition timeIsOut=new TimeOut(">> over"){
				  public void action(){
  					//SimpleButton.this.action(nbPress+" and a half click(s)");
  					System.out.println("timer out and nbPress " + nbPress);
  					nbPress--;
				  }
			  };
			  
		  };
		  
		   //when out of the shape
		  public State out = new State(){
			  
			  Transition releaseOut = new Release(button, ">>nothing"){};
			  Transition returnOnButton = new EnterOnTag(container, ">>click"){};
		  };
		  
		  
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
	   frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE) ;
	   frame.pack() ;
	   frame.setVisible(true) ;

	   SimpleButton simple = new SimpleButton(canvas, "simple") ;
	   simple.getShape().translateBy(100,100) ;
	   
	   JFrame visual = new JFrame();
	   visual.getContentPane().add(new StateMachineVisualization(simple.sm));
	   visual.setLocation(500, 500);
	   visual.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE) ;
	   visual.pack();
	   visual.setVisible(true);
    }

}
