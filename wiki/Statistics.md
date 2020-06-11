# Statistics #

* ** Statistic:** The standard type of Statistic, creates simple Experiment result sheet 

    * [DummyStatistic](DummyStatistic)
    * [MappingPrinter](MappingPrinter)
    * [MatchListPrinter](MatchListPrinter)
    * [SMBTrainingPrinter](SMBTrainingPrinter)

* ** K2Statistic: ** The most Common type of Statistic, the method of using it is to Initializes the statistic,
by giving the Match Information containing the similarity matrix to be compared with exact match and the exact match.

    * [AttributeNBGolden](AttributeNBGolden)
    * [BinaryGolden](BinaryGolden)
    * [BinaryROCStatistics](BinaryROCStatistics)
    * [ComplexBinaryGolden](ComplexBinaryGolden)
    * [EntryGolden](EntryGolden)
    * [K1Informed](K1Informed)
    * [L2distance](L2distance)
    * [MatchDistance](MatchDistance)
    * [MCC (Matthews correlation coefficient)](MCC)
    * [NBGolden](NBGolden)
    * [UnconstrainedMatchDistance](UnconstrainedMatchDistance)
    * [VectorPrinterUsingExact](VectorPrinterUsingExact)



* ** Predictor: ** Given attribute pair-wise similarity measures, a predictor predicts the success of a matcher in identifying correct correspondences (exact match)

    * **MatrixPredictors:**
        * [BMPredictor](BMPredictor)
        * [BMMPredictor](BMMPredictor)
        * [LMMPredictor](LMMPredictor)
        * [STDEVPredictor](STDEVPredictor)
        * [MaxPredictor](MaxPredictor)
        * [AvgPredictor](AvgPredictor)

    * **AttributePredictors:**
        * [BMPredictor](BMPredictor)
        * [OneToOneAPredictor](OneToOneAPredictor)
        * [STDEVPredictor](STDEVPredictor)
        * [MaxAPredictor](MaxAPredictor)
        * [AvgAPredictor](AvgAPredictor)

