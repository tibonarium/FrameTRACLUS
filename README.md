# FrameTRACLUS

Wrote graphical GUI for TRACLUS algorithm. Code for computations were borrowed from https://github.com/luborliu/TraClusAlgorithm 

Program contains several components:
- Window with GUI elements for file path,
button to start computations, text area for messages
and custom JPanel to display graphics.
- Even though computations take several miliseconds
to complete they were placed into special worker thread.