package shared.javaUtil;

public final class VariableConstants {
    
    // Private constructor prevents instantiation since this is a pure utility class
    private VariableConstants() {}

    // --- ANSI ESCAPE STYLE CONSTANTS ---
    public static final String RESET = "\033[0m";
    public static final String BOLD = "\033[1m";
    public static final String DIM = "\033[2m";
    public static final String UNDERLINE = "\033[4m";
    public static final String BLINK = "\033[5m";
    public static final String INVERT = "\033[7m";
    public static final String STRIKETHROUGH = "\033[9m";

    // --- FOREGROUND COLORS ---
    public static final String FG_BLACK = "\033[30m";
    public static final String FG_RED = "\033[31m";
    public static final String FG_GREEN = "\033[32m";
    public static final String FG_YELLOW = "\033[33m";
    public static final String FG_BLUE = "\033[34m";
    public static final String FG_MAGENTA = "\033[35m";
    public static final String FG_CYAN = "\033[36m";
    public static final String FG_WHITE = "\033[37m";

    // --- BACKGROUND COLORS ---
    public static final String BG_BLACK = "\033[40m";
    public static final String BG_RED = "\033[41m";
    public static final String BG_GREEN = "\033[42m";
    public static final String BG_YELLOW = "\033[43m";
    public static final String BG_BLUE = "\033[44m";
    public static final String BG_MAGENTA = "\033[45m";
    public static final String BG_CYAN = "\033[46m";
    public static final String BG_WHITE = "\033[47m";

    // --- 256-COLOR FUNCTIONAL METHODS ---
    // Replaces C parameter macros with type-safe methods
    public static String FG_COLOR(int id) {
        return "\033[38;5;" + id + "m";
    }

    public static String BG_COLOR(int id) {
        return "\033[48;5;" + id + "m";
    }

    // --- ADVANCED LAYOUT UTILITIES ---
    public static final String CLEAR_LINE = "\033[2K\r";

    // --- SCREEN & KEYBOARD CONTROLS ---
    public static final int ENDOFSTRING = 0;          // NUL termination
    public static final int BELL = 7;                 // Audio alert
    public static final int BACKSPACE = 8;            
    public static final int HORIZONTALTAB = 9;        
    public static final int LINEFEED = 10;            
    public static final int CARRIAGERETURN = 13;      
    public static final int ESCAPE = 27;              

    // --- DATA TRANSMISSIONS ---
    public static final int STARTOFHEADING = 1;       
    public static final int STARTOFTEXT = 2;          
    public static final int ENDOFTEXT = 3;            
    public static final int ENDOFTRANSMISSION = 4;    
    public static final int ENQUIRY = 5;              
    public static final int ACKNOWLEDGE = 6;          
    public static final int NEGATIVEACKNOWLEDGE = 21; 
    public static final int SYNCHRONOUSIDLE = 22;     
    public static final int ENDOFTRANSMITBLOCK = 23;  

    // --- PHYSICAL MACHINE CONTROL ---
    public static final int VERTICALTAB = 11;         
    public static final int FORMFEED = 12;            
    public static final int SHIFTOUT = 14;            
    public static final int SHIFTIN = 15;             
    public static final int DATALINKESCAPE = 16;      
    public static final int DEVICECONTROL1 = 17;      
    public static final int DEVICECONTROL2 = 18;      
    public static final int DEVICECONTROL3 = 19;      
    public static final int DEVICECONTROL4 = 20;      
    public static final int CANCEL = 24;              
    public static final int ENDOFMEDIUM = 25;         
    public static final int SUBSTITUTE = 26;          

    // --- ANCIENT DATABASE SEPARATORS ---
    public static final int FILESEPARATOR = 28;       
    public static final int GROUPSEPARATOR = 29;      
    public static final int RECORDSEPARATOR = 30;     
    public static final int UNITSEPARATOR = 31;       
    public static final int DELETECHAR = 127;         

    // --- UTILITIES ---
    public static final String SPINNER = "|/-\\"; 

    public static void NEWLINE() {
        System.out.print(RESET + "\n\n");
    }

    public static void LINEBREAK() {
        System.out.print(RESET + "\n--------------------------------\n");
    }
}
