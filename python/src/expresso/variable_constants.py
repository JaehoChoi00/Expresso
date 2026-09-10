
RESET          = "\033[0m"
BOLD           = "\033[1m"
DIM            = "\033[2m"
UNDERLINE      = "\033[4m"
BLINK          = "\033[5m"
INVERT         = "\033[7m"
STRIKETHROUGH  = "\033[9m"

FG_BLACK       = "\033[30m"
FG_RED         = "\033[31m"
FG_GREEN       = "\033[32m"
FG_YELLOW      = "\033[33m"
FG_BLUE        = "\033[34m"
FG_MAGENTA     = "\033[35m"
FG_CYAN        = "\033[36m"
FG_WHITE       = "\033[37m"

BG_BLACK       = "\033[40m"
BG_RED         = "\033[41m"
BG_GREEN       = "\033[42m"
BG_YELLOW      = "\033[43m"
BG_BLUE        = "\033[44m"
BG_MAGENTA     = "\033[45m"
BG_CYAN        = "\033[46m"
BG_WHITE       = "\033[47m"

FG_COLOR = lambda id: f"\033[38;5;{id}m"
BG_COLOR = lambda id: f"\033[48;5;{id}m" 

CLEAR_LINE     = "\033[2K\r"

ENDOFSTRING    = 0     
BELL           = 7            
BACKSPACE      = 8       
HORIZONTALTAB  = 9   
LINEFEED       = 10   
CARRIAGERETURN = 13
ESCAPE         = 27         

STARTOFHEADING = 1      
STARTOFTEXT    = 2         
ENDOFTEXT      = 3           
ENDOFTRANSMISSION = 4   
ENQUIRY = 5             
ACKNOWLEDGE = 6         
NEGATIVEACKNOWLEDGE = 21
SYNCHRONOUSIDLE = 22    
ENDOFTRANSMITBLOCK = 23  

VERTICALTAB = 11     
FORMFEED = 12        
SHIFTIN = 15         
DATALINKESCAPE = 16  
DEVICECONTROL1 = 17  
DEVICECONTROL2 = 18  
DEVICECONTROL3 = 19  
DEVICECONTROL4 = 20  
CANCEL = 24          
ENDOFMEDIUM = 25     
SUBSTITUTE = 26      

FILESEPARATOR = 28    
GROUPSEPARATOR = 29    
RECORDSEPARATOR = 30    
UNITSEPARATOR = 31    
DELETECHAR = 127      

SPINNER = "|/-\\" 
NEWLINE = lambda: print(RESET + "\n\n")
LINEBREAK = lambda: print(RESET + "\n--------------------------------\n")
