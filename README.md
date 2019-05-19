# P11
A functional, Lisp-like language designed by Dr. Jerry Shultz and the students of Principles of Programming Languages. 
P11 allows for a file of user-defined functions to be passed, giving the user the ability to enter commands through a *REPL*
(read-eval-print loop).

# Getting Started
Getting started with P11 is quite simple. Included with P11 are three test files located in the 
[user_defined](../src/user_defined) directory that can immediately be passed as arguments. Compile and run P11, then
give some of the examples below a shot!

```
$ cd src
$ javac *.java
$ java P11 test
```

#### A few examples of how to use the REPL with the file [test](../src/user_defined/test) passed as an arg:
```
> (plus 1 2) // Returns 3
> (minus (plus 1 2) (times 4 7)) // Returns -25
> (sum (1 2 3 4 5)) // Returns 15
> (len (3 4 5 (1 2 3) (4)) // Returns 5
> (ave (67 7 382 1 3 9)) // Returns 78.167
```

# The CFG
The general structure of the context-free grammar is given below. Definitions that are defined in files follow this structure
from the top down, while REPL commands follow the structure of a list, with the name being either a user-defined function or 
a built-in function.

---
```
<defs> --> <def> | <def> <defs>
<def> --> LPAREN DEFINE LPAREN NAME RPAREN <expr> RPAREN | 
            LPAREN DEFINE LPAREN NAME <params> RPAREN <expr> RPAREN
<params> --> NAME | NAME <params>
<expr> --> NUMBER | NAME | <list>
<list> --> LPAREN RPAREN | LPAREN <items> RPAREN | 
             LPAREN NAME RPAREN | LPAREN NAME <items> RPAREN
<items> --> <expr> | <expr> <items>
```
---

* NAME can be any function name or variable name.

* DEFINE is a keyword required for all user-defined definitions.

# Built-In Functions
P11 has multiple built-in functions that take in two, one, or zero arguments of either a number or a list.

#### These functions take numeric inputs and produce a numeric result:

|Call Form|Meaning|
|--------|--------|
|(plus x y)| x + y|
|(minus x y)| x - y|
|(times x y)| x * y|
|(div x y)| x / y|
|(lt x y)|Return 1 if x < y, otherwise return 0.|
|(le x y)|Return 1 if x <= y, otherwise return 0.|
|(eq x y)|Return 1 if x == y, otherwiser return 0.|
|(ne x y)|Return 1 if x != y, otherwiser return 0.|
|(and x y)|Return 1 if x and y are > 0, otherwiser return 0.|
|(or x y)|Return 1 if x or y is > 0, otherwiser return 0.|
|(not x)|Return 1 if x is 0, otherwise return 0.|

#### These Functions provide list manipulation abilities (the parameter y must be a list):

|Call Form|Meaning|
|---------|-------|
|(ins x y)|Return the list formed by *inserting* x in the beginning of a copy of y.|
|(first y)|Return a copy of the first item in y, with an error if y is empty (car in Lisp).|
|(rest y)|Return a copy of y with its first item removed (cdr in Lisp).|

#### These functions are known as *predicates* (informs the user about the state of the input):

|Call Form|Meaning|
|---------|-------|
|(null x)|Return 1 if x is (), otherwise return 0.|
|(num x)|Return 1 if x is a number, otherwise return 0.|
|(list x)|Return 1 if x is a list, otherwise return 0.|

#### These functions are useful in the REPL:

|Call Form|Meaning|
|---------|-------|
|(read)|Waits for the user to type a num from the keyboard and returns that value.|
|(write x)|Display on-screen the value of x, followed by a space.|
|(nl)|Start a new on-screen display line.|
|(quote x)|Return x without evaluation.|
|(quit)|Halt execution.|

# Structure
### [P11](../src/P11.java)
The P11 class is the main driver of the project. It can take the file name containing user-defined functions as an argument.

### [Lexer](../src/Lexer.java)
The Lexer class breaks apart P11 code into tokens through a series of states.

### [Parser](../src/Parser.java)
The Parser class examines tokens and deduces if a program is valid or not. If it is, it organizes the programming into a tree of nodes.

### [Node](../src/Node.java)
The Node class evaluates the program recursively, returning the answer to a given command after evaluation of a tree of nodes.

### [Token](../src/Token.java)
The Token class allows the Lexer to create token objects.

### [Value](../src/Value.java)
The Value class allows Node to create objects during evaluation that can either be a list or a number.

### [StackFrame](../src/StackFrame.java)
The StackFrame class allows for parameters for userdefined functions to be assigned to names, so that they may be called on later during evaluation.

### [Basic](../src/Basic.java), [Camera](../src/Camera.java), [TreeViewer](../src/TreeViewer.java)
These classes were built by Dr. Shultz to display the parse trees from either the user-defined functions or the REPL commands.

### [/user_defined](../src/user_defined)
Contains all user_defined functions, including three as an example of how user-defined functions should be structured.

## Notes
* Currently, only the user_defined functions in [test](../src/user_defined/test) are actually working.
* In order for the other two to work, [Parser](../src/Parser.java) may need some re-building.

## TODO
- [ ] Make changes to how lists containing functions are parsed.
- [ ] Successfully test [pastri](../src/user_defined/pastri) and [binTreeIOT](../src/user_defined/binTreeIOT).
- [ ] Remove unnecessary comments.
- [ ] Simplify and clean up some areas in [Parser](../src/Parser.java) and [Node](../src/Node.java).
- [ ] Remove any unecessary states from [Lexer](../src/Lexer.java).
