package com.swabunga.spell.engine;

/** JMH WHY have this class? Why arent the variables just static in the
 *  EditDistance class?. I have made them final as a start.
 */
public class EditDistanceWeights {

    final int del1 = 95;
    final int del2 = 95;
    final int swap = 90;
    final int sub = 100;
    final int similar = 10;
    final int min = 90;
    final int max = 100;

}
