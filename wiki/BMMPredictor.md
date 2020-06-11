## BMMPredictor ##

Implementation of the Predictor interface.

Returns the cosine similarity between the result vector and the closest binary vector having at least k  entries equaling 1.

Finds this vector by setting all entries over 0.5 to 1 and then completing to k entries by taking the top entries below 0.5 and
rounding them to 1.