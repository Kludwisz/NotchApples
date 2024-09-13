import com.seedfinding.mccore.rand.ChunkRand;
import com.seedfinding.mccore.util.data.Pair;
import com.seedfinding.mccore.util.pos.BPos;
import com.seedfinding.mcfeature.loot.LootTable;
import kludwisz.ancientcity.AncientCityGenerator;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class AncientCityChestHeatmap {
    public static void main(String[] args) {
        final double heatScaler = 2.0D;
        final long sampleSize = 2_000_000L;
        AncientCityGenerator gen = new AncientCityGenerator();
        ChunkRand rand = new ChunkRand();

        int [][] heatmap = new int[21][21];

        for (long seed = 0; seed < sampleSize; seed++) {
            gen.generate(seed, 10, 10, rand);

            for (Pair<BPos, LootTable> chest : gen.getChests()) {
                BPos chestPos = chest.getFirst();
                if (chest.getSecond().lootPools.length != 2)
                    continue; // not normal chest

                int x = chestPos.getX() >> 4;
                int z = chestPos.getZ() >> 4;
                heatmap[x][z]++;
            }
        }

        double [][] avgChestCountHeatmap = new double[21][21];
        for (int x = 0; x < 21; x++) {
            for (int z = 0; z < 21; z++) {
                avgChestCountHeatmap[x][z] = (double)heatmap[x][z] / sampleSize * heatScaler;
            }
        }

        createHeatmap(avgChestCountHeatmap, 64, "heatmap.png");
    }

    // Method to create a heatmap and save it as an image (courtesy of chatgpt)
    // Method to create a heatmap, scale it, add coordinates, and save it as an image
    public static void createHeatmap(double[][] data, int cellSize, String outputFilePath) {
        int rows = data.length;       // 21 rows
        int cols = data[0].length;    // 21 columns
        int width = cols * cellSize;  // Scaled width
        int height = rows * cellSize; // Scaled height

        // Create a BufferedImage with scaled dimensions
        BufferedImage heatmap = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // Get the Graphics2D object for drawing
        Graphics2D g2d = heatmap.createGraphics();

        // Set the font for drawing the coordinates
        g2d.setFont(new Font("Arial", Font.BOLD, 14));

        // Loop over the 2D array and map each value to a color and add text
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                // Normalize the value in the range [0, 1]
                double value = data[i][j];
                if (value < 0) value = 0;
                if (value > 1) value = 1;

                // Map the value to a color (blue for low values, red for high values)
                Color color = getColorFromValue(value);

                // Set the color and fill a 32x32 pixel block for each array element
                g2d.setColor(color);
                g2d.fillRect(j * cellSize, i * cellSize, cellSize, cellSize);

                // Draw the coordinates (i, j) in the center of each cell
                g2d.setColor(Color.WHITE); // Set text color to white for visibility
                String text = "(" + (i-10) + "," + (j-10) + ")";
                g2d.drawString(text, j * cellSize + 40 - text.length() * 4, i * cellSize + cellSize / 2 + 5);
            }
        }

        // Dispose the Graphics2D object
        g2d.dispose();

        // Save the image to a file
        try {
            File outputFile = new File(outputFilePath);
            ImageIO.write(heatmap, "png", outputFile);
            System.out.println("Heatmap saved to " + outputFilePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Helper method to map a value in [0, 1] range to a color
    private static Color getColorFromValue(double value) {
        // Linear interpolation between blue and red
        int r = (int) (255 * value);  // Red component
        int g = 0;                    // Green component stays 0
        int b = (int) (255 * (1 - value)); // Blue component

        return new Color(r, g, b);
    }
}
