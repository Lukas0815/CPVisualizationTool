package Input;

import javafx.scene.paint.Color;

public class ColorScheme {

    private Color blockingColor, diagonalColor, shortColor, criticalColor, directColor, chamberColor;
    private double heatOpacity;

    public ColorScheme(){
        this.blockingColor = Color.GRAY;
        this.diagonalColor = Color.YELLOW;
        this.shortColor = Color.GREEN;

        this.heatOpacity = 0.3;
    }

    private void loadDefaultScheme(){
        
    }

    public Color getBlockingColor() {
        return blockingColor;
    }

    public void setBlockingColor(Color blockingColor) {
        this.blockingColor = blockingColor;
    }

    public Color getDiagonalColor() {
        return diagonalColor;
    }

    public void setDiagonalColor(Color diagonalColor) {
        this.diagonalColor = diagonalColor;
    }

    public Color getShortColor() {
        return shortColor;
    }

    public void setShortColor(Color shortColor) {
        this.shortColor = shortColor;
    }

    public Color getCriticalColor() {
        return criticalColor;
    }

    public void setCriticalColor(Color criticalColor) {
        this.criticalColor = criticalColor;
    }

    public Color getDirectColor() {
        return directColor;
    }

    public void setDirectColor(Color directColor) {
        this.directColor = directColor;
    }

    public Color getChamberColor() {
        return chamberColor;
    }

    public void setChamberColor(Color chamberColor) {
        this.chamberColor = chamberColor;
    }

    public double getHeatOpacity() {
        return heatOpacity;
    }

    public void setHeatOpacity(double heatOpacity) {
        this.heatOpacity = heatOpacity;
    }
}
