#ifndef VARIABLECONSTANTS_H
#define VARIABLECONSTANTS_H

// --- ANSI ESCAPE STYLE MACROS ---
#define RESET "\033[0m"
#define BOLD "\033[1m"
#define DIM "\033[2m"
#define UNDERLINE "\033[4m"
#define BLINK "\033[5m"
#define INVERT "\033[7m"
#define STRIKETHROUGH "\033[9m"

// --- FOREGROUND COLORS ---
#define FG_BLACK "\033[30m"
#define FG_RED "\033[31m"
#define FG_GREEN "\033[32m"
#define FG_YELLOW "\033[33m"
#define FG_BLUE "\033[34m"
#define FG_MAGENTA "\033[35m"
#define FG_CYAN "\033[36m"
#define FG_WHITE "\033[37m"

// --- BACKGROUND COLORS ---
#define BG_BLACK "\033[40m"
#define BG_RED "\033[41m"
#define BG_GREEN "\033[42m"
#define BG_YELLOW "\033[43m"
#define BG_BLUE "\033[44m"
#define BG_MAGENTA "\033[45m"
#define BG_CYAN "\033[46m"
#define BG_WHITE "\033[47m"

// --- 256-COLOR FUNCTIONAL MACROS ---
#define FG_COLOR(id) "\033[38;5;" #id "m" // Sets text color by ID (0-255)
#define BG_COLOR(id) "\033[48;5;" #id "m" // Sets background color by ID (0-255)

// --- ADVANCED LAYOUT UTILITIES ---
#define CLEAR_LINE "\033[2K\r"

// --- SCREEN & KEYBOARD CONTROLS ---
#define ENDOFSTRING 0     // ASCII code for NUL / NULL pointer termination
#define BELL 7            // ASCII code to play system audio alert sound
#define BACKSPACE 8       // ASCII code to move terminal cursor backward one space
#define HORIZONTALTAB 9   // ASCII code to shift text by a tab key column amount
#define LINEFEED 10       // ASCII code for \n to drop cursor down to next line
#define CARRIAGERETURN 13 // ASCII code for \r to return cursor to start of current line
#define ESCAPE 27         // ASCII code for ESC to start terminal formatting commands

// --- DATA TRANSMISSIONS ---
#define STARTOFHEADING 1       // Marks the beginning of metadata (like file name or address)
#define STARTOFTEXT 2          // Signals that the actual body of the message is starting
#define ENDOFTEXT 3            // Signals that the body of the message is finished
#define ENDOFTRANSMISSION 4    // Closes the raw active data transmission stream
#define ENQUIRY 5              // Asks the remote device "Are you there? Send your status."
#define ACKNOWLEDGE 6          // The receiving device replies "Yes, I am here and ready."
#define NEGATIVEACKNOWLEDGE 21 // The receiver replies "Error! Last data packet corrupted, resend."
#define SYNCHRONOUSIDLE 22     // Sent periodically to keep two communications devices in sync
#define ENDOFTRANSMITBLOCK 23  // Marks the end of a single chunk when dividing massive files

// --- PHYSICAL MACHINE CONTROL ---
#define VERTICALTAB 11    // Jumped the paper roller downward to a pre-set row
#define FORMFEED 12       // Ejected the current physical piece of paper out for a fresh page
#define SHIFTOUT 14       // Switched mechanical printer ribbons to alternate color/font (e.g., Red)
#define SHIFTIN 15        // Switched printer ribbon back to the default black/standard font
#define DATALINKESCAPE 16 // Changes meaning of the very next character to a raw hardware command
#define DEVICECONTROL1 17 // Custom hardware switch. Universally used as "XON" to resume reader
#define DEVICECONTROL2 18 // Custom hardware switch for secondary attached device operations
#define DEVICECONTROL3 19 // Custom hardware switch. Universally used as "XOFF" to pause reader
#define DEVICECONTROL4 20 // Custom hardware switch for secondary attached device operations
#define CANCEL 24         // Tells mechanical printer to ignore everything typed on current line
#define ENDOFMEDIUM 25    // Triggered alarm indicating machine was out of paper tape or ink ribbon
#define SUBSTITUTE 26     // Replaced a character that couldn't be printed due to data corruption

// --- ANCIENT DATABASE SEPARATORS ---
#define FILESEPARATOR 28   // Acted like a modern folder boundary to separate files in a stream
#define GROUPSEPARATOR 29  // Acted like a sub-folder boundary to separate records within a file
#define RECORDSEPARATOR 30 // Acted like a spreadsheet row boundary to separate individual records
#define UNITSEPARATOR 31   // Acted like a spreadsheet column boundary to separate fields in a record
#define DELETECHAR 127     // ASCII for DEL (1111111) used to physically punch holes over tape mistakes

// --- UTILITIES ---
#define SPINNER "|/-\\"              // Characters for a quick downloading/loading animation loop
#define NEWLINE printf(RESET "\n\n") // \n is equivalent to LINEFEED
#define LINEBREAK printf(RESET "\n--------------------------------\n")

#endif

