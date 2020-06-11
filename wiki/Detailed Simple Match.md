## Detailed Simple Match Experiment ##
The Detailed Simple Match, allows you to identify True Positives and False Negatives for each correspondence.
For more information about Percision,Recall,TP and FN please click [here](http://en.wikipedia.org/wiki/Precision_and_recall).

The only adjustment you need to do, is enter the argument SimpleMatchD instead of SimpleMatch.

For example: if you are Running an experiment through Eclipse:

cmd C:\results SimpleMatchD 0 35 0 -f:0,1,2,3 -s:1,3

Or if you are Running an experiment through the server:

java -jar ORE.jar cmd ./res SimpleMatchD 0 35 0 -f:0,1,2,3 -s:1,3