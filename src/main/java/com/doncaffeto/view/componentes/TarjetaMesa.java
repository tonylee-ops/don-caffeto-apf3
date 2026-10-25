package com.doncaffeto.view.componentes;

import com.doncaffeto.model.Mesa;
import com.doncaffeto.util.Tema;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JButton;

public class TarjetaMesa extends JButton {
    private Mesa mesa;

    public TarjetaMesa(Mesa mesa) {
        this.mesa = mesa;
        setPreferredSize(new Dimension(200, 150));
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setToolTipText(mesa.isLibre() ? "Tomar pedido" : "Ver pedido de la mesa");
    }

    public Mesa getMesa() {
        return mesa;
    }

    public void setMesa(Mesa mesa) {
        this.mesa = mesa;
        setToolTipText(mesa.isLibre() ? "Tomar pedido" : "Ver pedido de la mesa");
        repaint();
    }

    public static String cronometro(long segundos) {
        long h = segundos / 3600;
        long m = segundos % 3600 / 60;
        long s = segundos % 60;
        return h > 0 ? String.format("%d:%02d:%02d", h, m, s) : String.format("%02d:%02d", m, s);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        Color color = Tema.colorMesa(mesa.getEstado());
        if (getModel().isRollover()) {
            color = color.darker();
        }
        int w = getWidth();
        int h = getHeight();
        g2.setColor(color);
        g2.fillRoundRect(0, 0, w - 1, h - 1, 22, 22);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 26));
        g2.drawString("Mesa " + mesa.getNumero(), 18, 40);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 13));
        g2.drawString(mesa.getCapacidad() + " personas", 18, 62);

        g2.setFont(new Font("SansSerif", Font.BOLD, 14));
        g2.drawString(Tema.textoMesa(mesa.getEstado()), 18, h - 22);
        if (!mesa.isLibre()) {
            String tiempo = cronometro(mesa.getSegundosOcupada());
            g2.setFont(new Font("Monospaced", Font.BOLD, 22));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(tiempo, w - fm.stringWidth(tiempo) - 18, h - 20);
        }
        g2.dispose();
    }
}
