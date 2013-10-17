package com.alexdiru.criticalerror;

import java.util.ArrayList;
import java.util.Random;

import android.graphics.Point;
import android.util.Log;

public class DataCliffGenerator {
	
	private static Random random = new Random();
	
	/**
	 * Generates the random cliffs of the planet
	 * @param maxHeight The maximum height of the cliffs
	 * @param minHeight The minimum height of the cliffs
	 * @param numPeaks A rough estimate for the number of peaks the cliffs will have
	 * @param numFlats A rough estimate for the number of flats the cliffs will have
	 * @param seed The seed used to generate the level
	 * @return The clifflines generated
	 */
	public static DataCliffLine[] generate(int maxHeight, int minHeight, int numPeaks, int numFlats, long seed) {
		Log.d("generate", "GENERATE_START");
		
		random.setSeed(seed);
		
		//Each peak will have 2 lines: /\
		//Each flat will have a single line: _
		ArrayList<DataCliffLine> cliffLines = new ArrayList<DataCliffLine>();
		
		//Stores the end point of thse line just added
		//Initially will store the start position of the cliffs
		Point currentLineEnd = new Point(0, random.nextInt(maxHeight-minHeight) + minHeight);
		
		//Use a constant width for each peak/flat
		int minWidth = (int)DataLander.mLanderWidth;
		int maxWidth = 150;
		
		int cumulativeCliffWidth = 0;
		int width;
		
		//Whether a flat can be placed down
		boolean flatAllowed = false;
		
		while (cumulativeCliffWidth < GameThread.mWorldWidth) {
			
			//If not enough width on right side of screen for player to be able to land safely
			if (GameThread.mWorldWidth - cumulativeCliffWidth < maxWidth)
				flatAllowed = false;
			
			Point newLineStart;
			
			//Randomly choose between a peak or flat
			if (random.nextInt(numPeaks+numFlats) < numFlats && flatAllowed) {
				numFlats--;
				
				//Flat
				
				width = random.nextInt(maxWidth-minWidth) + minWidth;
				cumulativeCliffWidth += width;
				
				newLineStart = new Point(currentLineEnd.x + width, currentLineEnd.y);
				
				cliffLines.add(new DataCliffLine(currentLineEnd.x, currentLineEnd.y, newLineStart.x, newLineStart.y, DataCliffLine.LINETYPE_FLAT, random));	
				currentLineEnd = newLineStart;
				
				//Can't place a flat next to another flat
				flatAllowed = false;
			} else if (random.nextInt(2) == 0) {
				if (numPeaks > 2)
					numPeaks--;
				//Peak
				
				//Ascending peak line
				width = random.nextInt(maxWidth-minWidth) + minWidth;
				cumulativeCliffWidth += width;
				
				if (maxHeight != currentLineEnd.y)
					newLineStart = new Point(currentLineEnd.x + width, currentLineEnd.y + random.nextInt(maxHeight - currentLineEnd.y));
				else
					newLineStart = new Point(currentLineEnd.x + width, currentLineEnd.y);
				
				cliffLines.add(new DataCliffLine(currentLineEnd.x, currentLineEnd.y, newLineStart.x, newLineStart.y, DataCliffLine.LINETYPE_ASCENDING, random));
				currentLineEnd = newLineStart;
				flatAllowed = true;
			} else {

				//Descending peak line
				width = random.nextInt(maxWidth-minWidth) + minWidth;
				cumulativeCliffWidth += width;
				
				if (currentLineEnd.y > minHeight)
					newLineStart = new Point(currentLineEnd.x + width, currentLineEnd.y - random.nextInt(currentLineEnd.y - minHeight));
				else
					newLineStart = new Point(currentLineEnd.x + width, currentLineEnd.y);
				
				cliffLines.add(new DataCliffLine(currentLineEnd.x, currentLineEnd.y, newLineStart.x, newLineStart.y, DataCliffLine.LINETYPE_DESCENDING,random));
				currentLineEnd = newLineStart;
				
				//Can place a flat next line
				flatAllowed = true;
			}
		}
		
		//Cast list to array and return it
		DataCliffLine[] cliffLineArray = new DataCliffLine[cliffLines.size()];
		cliffLines.toArray(cliffLineArray);
		return cliffLineArray;
	}
}
