import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by shankar pentyala on 25-07-2017.
 */
public class Appletclass extends JApplet implements ActionListener {
    JButton button;
    JTextField text;
    JLabel l;
    String ans="";
    public void init() {
        this.setSize(1000, 500);
        //this.add(paint(g));
        this.add(getCustPanel());
        setForeground(Color.yellow);
        setBackground(Color.red);
        this.setVisible(true);


       repaint();
    }
    public void paint(Graphics g) {
        g.drawString("Welcome in Java Applet.",400,200);
    }

    private JPanel getCustPanel() {
        JPanel panel = new JPanel();
        //  panel.setLayout ((LayoutManager) new BoxLayout(panel, BoxLayout.Y_AXIS));

        text=new JTextField();
        text.setPreferredSize( new Dimension( 400, 20 ) );
        text.setToolTipText("Please enter your question");
        //text.setAlignmentX(Component.CENTER_ALIGNMENT);
        text.setAlignmentX(500);
        text.setAlignmentY(900);
        button = new JButton("Ask");
        button.setPreferredSize(new Dimension(100, 20));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.addActionListener(this);
        panel.add(text);
        panel.add(button);
        l=new JLabel();
      //  text.setPreferredSize( new Dimension( 400, 20 ) );
        l.setAlignmentX(Component.HEIGHT );
        panel.add(l);
        return panel;
    }

    public void actionPerformed(ActionEvent e) {
        try {
           // Question2Query q = new Question2Query();
            ans = Question2Query.main(text.getText());
            l.setText("\n"+ans);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

    }
}
