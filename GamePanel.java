package uk.ac.cam.ld558.oop.tick5;

import uk.ac.cam.ld558.oop.tick2.ArrayWorld;
import uk.ac.cam.ld558.oop.tick2.World;
import uk.ac.cam.ld558.oop.tick3.PatternFormatException;

import java.awt.*;
import javax.swing.JPanel;

public class GamePanel extends JPanel {

    private World world;

    @Override
    protected void paintComponent(java.awt.Graphics g) {
        // Paint the background white
        g.setColor(java.awt.Color.WHITE);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        System.out.println("hello");
        if (world != null) {
            int yaccomodate = this.getHeight() / world.getHeight();
            int xaccomodate = this.getWidth() / world.getWidth();
            int sqconstraint = 0; /*How large can the square dimension conceivably be given the GOL board and the size of the gamepanel?*/
            int sqremainder = 0;/*Pixels do not divide exactly*/
            if (yaccomodate<xaccomodate){ /*If more squares must be formed along the y-axis*/
                sqconstraint = yaccomodate; /*Set the dimensions of the square to that which can be appropriately accommodated alone the y-axis.*/
                sqremainder = this.getHeight() % world.getHeight();
            }
            else {
                sqconstraint = xaccomodate;
                sqremainder = this.getWidth() % world.getWidth();
            }
            boolean canfitremaindery;
            boolean canfitremainderx;
            for (int i = 0; i < world.getHeight() ; i++) {
                canfitremaindery = (i<sqremainder); /*If the number of square being formed is less than the remainder, then an additional
                pixel can be accomodated into that square, must be done to preserve aesthetic appeal due to quantisation errorr associated
                with fitting perfect squares into a static drawing environment.*/
                for (int j = 0; j < world.getWidth() ; j++) {
                    canfitremainderx = (j<sqremainder);
                    if (world.getCell(i,j)){
                        g.setColor(Color.BLACK);
                        /*If quantisation error must be factored in then shift the position of square creation accordingly and add an additoinal pixel. Else,
                        factor in the shift but making no alteration to square dimension.*/
                        g.fillRect(sqconstraint*j +(canfitremainderx ? j : sqremainder ), sqconstraint*i + (canfitremaindery ? i : sqremainder), sqconstraint + (canfitremainderx ? 1 :0), sqconstraint + (canfitremaindery ? 1 :0));

                    }
                    else{
                        g.setColor(Color.LIGHT_GRAY);
                        g.drawRect(sqconstraint*j +(canfitremainderx ? j : sqremainder ), sqconstraint*i + (canfitremaindery ? i : sqremainder), sqconstraint + (canfitremainderx ? 1 :0), sqconstraint + (canfitremaindery ? 1 :0));

                    }
                }

            }
            g.setColor(Color.BLACK);
            g.drawString("Generation: " + world.getGenerationCount() , 12, this.getHeight()-18);
            /*Parameters arbitrarily chosen from observation.*/
        }
    }

    public void display(World w) {
        world = w;
        repaint();
    }
}