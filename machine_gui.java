import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class machine_gui {
    // The private machine, static so I can edit it easily.
    private static Machine turing_machine;
    // Control display of turing_machine tape

    JFrame frame;
    // For error messages
    StatusBar status;
    // Panel A is the main tape
    JPanel panelA;
    JLabel tape;

    // Panel B has the state it's current in
    JPanel panelB;
    JLabel state;
    // Panel C has Buttons and tape change;
    JPanel panelC;
    JButton run;
    JButton step;
    JButton reset;
    JLabel tapeInstruction;
    JTextField changeTape;
    JButton inputTape;

    // Panel D is selecting file or inputting
    JPanel panelD;
    JLabel jcomboI;
    JComboBox<String> jcombo;
    JLabel or;
    JLabel instruction;
    JTextField tapeInput;
    JButton input;

    public machine_gui() {
        frame = new JFrame("Turing Machine");
        frame.setSize(750, 250);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(5,1));

        panelA = createPanelA();
        panelB = createPanelB();
        panelC = createPanelC();
        panelD = createPanelD();

        frame.add(panelA);
        frame.add(panelB);
        frame.add(panelC);
        frame.add(panelD);

        status = new StatusBar();
        status.setMessage("Error messages will appear here");
        frame.add(status);

    }

    JPanel createPanelA() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        tape = new JLabel("<html>[ t u r i n g   <u><font color='red'> m</font></u> a c h i n e ]</html>");
        tape.setFont(new Font("Serif", Font.PLAIN, 25));
        panel.add(tape);

        return panel;
    }

    JPanel createPanelB() {        
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1,2));

        state = new JLabel("State: Initial");
        state.setFont(new Font("Serif", Font.PLAIN, 25));
        state.setHorizontalAlignment(JLabel.CENTER);
        panel.add(state);

        return panel;
    }

    JPanel createPanelC() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        run = new JButton("Run");
        run.addActionListener(new runMachine());
        panel.add(run);

        step = new JButton("Step");
        step.addActionListener(new stepMachine());
        panel.add(step);

        reset = new JButton("Reset");
        reset.addActionListener(new resetMachine());
        panel.add(reset);

        tapeInstruction = new JLabel("Change tape (string): ");
        panel.add(tapeInstruction);

        changeTape = new JTextField("", 20);
        panel.add(changeTape);

        inputTape = new JButton("Input");
        inputTape.addActionListener(new changeMachineTape());
        panel.add(inputTape);

        return panel;
    }

    JPanel createPanelD() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        jcomboI = new JLabel("Select Example Machine: ");
        panel.add(jcomboI);

        jcombo = new JComboBox<>(new String[] {"even_a.txt", "palindrome.txt", "anbncn.txt", "fibonacci.txt"});
        // I don't like the anonymous class, but it works
        jcombo.addActionListener(new ActionListener () {
            public void actionPerformed(ActionEvent e) {
                machine_gui.turing_machine = Machine.createMachine((String)jcombo.getSelectedItem());
                updateTape();
            }
        });
        panel.add(jcombo);

        or = new JLabel("or");
        panel.add(or);

        instruction = new JLabel("Input Machine File: ");
        panel.add(instruction);

        tapeInput = new JTextField("", 20);
        panel.add(tapeInput);

        input = new JButton("Input");
        panel.add(input);
        input.addActionListener(new getFileInput());

        return panel;
    }

    /**
     * Updates tape, state, and status of machine
     */
    void updateTape() {
        if (machine_gui.turing_machine.tape == null) {
            tape.setText("[ ]");
            status.setMessage("Tape is empty");
            return;
        }

        state.setText("State: " + machine_gui.turing_machine.state);

        // For highlighting, need to use html which is annoying, so i have to build it piece by piece
        StringBuilder str = new StringBuilder();
        // Store locally so don't have to constantlly access
        int tpos = machine_gui.turing_machine.tape.pos;
        char[] tp = machine_gui.turing_machine.tape.tape;

        str.append("<html>[ ");

        
        // Append all characters up to current char
        for (int i = 0; i < tpos; i++) {
            str.append(tp[i]+" ");
        }

        // Append underlined character
        str.append("<u><font color='red'>");
        str.append(tp[tpos]);
        str.append("</font></u>");

        for (int i = tpos+1; i < tp.length; i++) {
            str.append(" "+tp[i]);
        }

        str.append(" ]</html>");
        tape.setText(str.toString());
    }

    /**
     * Attempts to create a machine from a fileName input. If invalid, will display error message.
     */
    private class getFileInput implements ActionListener 
	{
		@Override
		public void actionPerformed(ActionEvent arg0) {
            String fileName = tapeInput.getText();
            Machine m = Machine.createMachine(fileName);

            // Check if no machine created yet
            if (m == null) {
                status.setMessage("Invalid Machine Entry. Check formatting of input file.");
                return;
            }

            status.setMessage("Machine imported. See console for description.");
            machine_gui.turing_machine = m;
            machine_gui.turing_machine.printMachine();
            updateTape();
        }
    }

    private class changeMachineTape implements ActionListener 
	{
		@Override
		public void actionPerformed(ActionEvent arg0) {
            if (machine_gui.turing_machine == null) {
                status.setMessage("Please import a machine before changing the tape.");
                return;
            }

            String newTape = changeTape.getText();
            machine_gui.turing_machine.reset();
            machine_gui.turing_machine.changeTape(newTape);
            updateTape();


            status.setMessage("Tape changed.");
        }
    }

    private class runMachine implements ActionListener 
	{
		@Override
		public void actionPerformed(ActionEvent arg0) {
            if (machine_gui.turing_machine == null) {
                status.setMessage("Please import a machine before running.");
                return;
            }

            if (machine_gui.turing_machine.state.equals(machine_gui.turing_machine.accept) || machine_gui.turing_machine.state.equals(machine_gui.turing_machine.reject)) {
                status.setMessage("Please reset machine before running.");
                return;
            }

            machine_gui.turing_machine.reset();
            machine_gui.turing_machine.run();
            updateTape();

            status.setMessage("Machine Ran");
        }
    }

    private class stepMachine implements ActionListener 
	{
		@Override
		public void actionPerformed(ActionEvent arg0) {
            if (machine_gui.turing_machine == null) {
                status.setMessage("Please import a machine before running.");
                return;
            }

            if (machine_gui.turing_machine.state.equals(machine_gui.turing_machine.accept) || machine_gui.turing_machine.state.equals(machine_gui.turing_machine.reject)) {
                status.setMessage("Please reset machine before running.");
                return;
            }

            try {
                Tuple configuration = new Tuple(machine_gui.turing_machine.state, machine_gui.turing_machine.tape.readChar());
                Tuple next_configuration = machine_gui.turing_machine.transitions.get(configuration);

                if (next_configuration == null) {
                    //throw new Exception("No transition avaliable");
                    System.out.println(configuration);
                    status.setMessage("No transition available, machine halting");
                    state.setText("State: Halted");
                    return;
                }

                machine_gui.turing_machine.state = next_configuration.state;
                if (next_configuration.ch != '$') {
                    machine_gui.turing_machine.tape.changeChar(next_configuration.ch);
                }
                
                try {
                    machine_gui.turing_machine.tape.move(next_configuration.direction);
                    updateTape();
                } catch (Tape.BeginningOfTape e) {
                    status.setMessage("Error: Attempted to move past the beginning of the tape. Machine halted.");
                    state.setText("State: Halted");
                }
            } catch (Tape.CharNotInAlphabet e) {
                status.setMessage("Error: Char not in alphabet, halting machine");
                state.setText("State: Halted");
                return;                
            }
            
        }
    }

    private class resetMachine implements ActionListener 
	{
		@Override
		public void actionPerformed(ActionEvent arg0) {
            if (machine_gui.turing_machine == null) {
                status.setMessage("Please import a machine before running.");
                return;
            }

            machine_gui.turing_machine.state = machine_gui.turing_machine.initial;
            machine_gui.turing_machine.tape.reset();
            updateTape();
        }
    }
    
}