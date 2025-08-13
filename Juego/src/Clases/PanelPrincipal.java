package Clases;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.imageio.ImageIO;
import java.util.Random;

public class PanelPrincipal extends JPanel {

    private static final long serialVersionUID = 1L;

    private int rectWidth;
    private int rectHeight;
    private int separacion;
    private int y1;
    private int y2;
    private int margen;
    private int tamanoletra;

    private int pelotaX, pelotaY;
    private int pelotaSize;
    private int pelotaVelX = 10;
    private int pelotaVelY = 10;
    private int golesIzquierda = 0;
    private int golesDerecha = 0;


    private int segundosTotales = 90; 
    private Timer timerJuego;         
    private Timer timerCuenta;       

    private BufferedImage fondo;
    private BufferedImage pelota;
    private BufferedImage paletaRoja;
    private BufferedImage paletaAzul;
    private final Set<Integer> teclasPresionadas = new HashSet<>();
    private boolean paletasCentradas = false;

    public PanelPrincipal() {
        setFocusable(true);

        try { fondo = ImageIO.read(getClass().getResource("/imagenes/fondo.jpg")); } 
        catch (IOException e) { System.out.println("No se pudo cargar la imagen de fondo"); }

        try { pelota = ImageIO.read(getClass().getResource("/imagenes/pelotin.png")); } 
        catch (IOException e) { System.out.println("No se pudo cargar la imagen de la pelota"); }

        try { paletaAzul = ImageIO.read(getClass().getResource("/imagenes/paleta azul derecha.jpeg")); } 
        catch (IOException e) { System.out.println("No se pudo cargar la imagen de la paleta azul"); }

        try { paletaRoja = ImageIO.read(getClass().getResource("/imagenes/paleta roja.jpeg")); } 
        catch (IOException e) { System.out.println("No se pudo cargar la imagen de la paleta roja"); }

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                teclasPresionadas.add(e.getKeyCode());
            }

            @Override
            public void keyReleased(KeyEvent e) {
                teclasPresionadas.remove(e.getKeyCode());
            }
        });


        timerJuego = new Timer(10, e -> {
            int panelHeight = getHeight();
            int velocidad = (int) (panelHeight * 0.015);

            if (teclasPresionadas.contains(KeyEvent.VK_W)) y1 -= velocidad;
            if (teclasPresionadas.contains(KeyEvent.VK_S)) y1 += velocidad;
            if (teclasPresionadas.contains(KeyEvent.VK_UP)) y2 -= velocidad;
            if (teclasPresionadas.contains(KeyEvent.VK_DOWN)) y2 += velocidad;

            clampPositions();

            pelotaX += pelotaVelX;
            pelotaY += pelotaVelY;

            if (pelotaY <= (int) (getHeight() * 0.134) || pelotaY + pelotaSize >= getHeight()) {
                pelotaVelY *= -1;
            }

            if (pelotaX <= separacion + rectWidth &&
                pelotaY + pelotaSize >= y1 && pelotaY <= y1 + rectHeight) {
                pelotaVelX *= -1.1;
                pelotaX = separacion + rectWidth;
            }

            if (pelotaX + pelotaSize >= getWidth() - rectWidth - separacion &&
                pelotaY + pelotaSize >= y2 && pelotaY <= y2 + rectHeight) {
                pelotaVelX *= -1.1;
                pelotaX = getWidth() - rectWidth - separacion - pelotaSize;
            }

            Random rand = new Random();
            int aleatorio = rand.nextInt(21) - 10;

            if (pelotaX < 0) {
                golesDerecha++;
                posicionarPelotaCentro();
                pelotaVelX = -10;
                pelotaVelY = aleatorio;
            }

            if (pelotaX > getWidth()) {
                golesIzquierda++;
                posicionarPelotaCentro();
                pelotaVelX = 10;
                pelotaVelY = aleatorio;
            }

            repaint();
        });
        timerJuego.start();


        timerCuenta = new Timer(1000, e -> {
            segundosTotales--;
            if (segundosTotales <= 0) {
                segundosTotales = 0;

                timerJuego.stop();
                timerCuenta.stop();


                new Timer(2000, ev -> {
                    segundosTotales = 90; 
                    golesIzquierda = 0;
                    golesDerecha = 0;
                    posicionarPelotaCentro();
                    posicionarPaletasCentro();

                    // Reiniciar timers
                    timerJuego.start();
                    timerCuenta.start();
                }) {{
                    setRepeats(false);
                }}.start();
            }
            repaint();
        });
        timerCuenta.start();
    }

    private void clampPositions() {
        int panelHeight = getHeight();
        int topMargin = (int) (panelHeight * 0.134);

        y1 = Math.max(topMargin, Math.min(y1, panelHeight - rectHeight));
        y2 = Math.max(topMargin, Math.min(y2, panelHeight - rectHeight));
    }

    private void posicionarPaletasCentro() {
        int panelHeight = getHeight();
        y1 = (panelHeight - rectHeight) / 2;
        y2 = (panelHeight - rectHeight) / 2;
    }

    private void posicionarPelotaCentro() {
        pelotaX = (getWidth() - pelotaSize) / 2;
        pelotaY = (getHeight() - pelotaSize) / 2;
    }

    private String formatearTiempo(int segundos) {
        int minutos = segundos / 60;
        int seg = segundos % 60;
        return String.format("%02d:%02d", minutos, seg);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int panelWidth = getWidth();
        int panelHeight = getHeight();

        if (fondo != null) {
            g.drawImage(fondo, 0, 0, panelWidth, panelHeight, null);
        }

        rectWidth = (int) (panelWidth * 0.02);
        rectHeight = (int) (panelHeight * 0.2);
        separacion = (int) (panelWidth * 0.025);

        pelotaSize = (int) (panelHeight * 0.05);

        if (!paletasCentradas) {
            posicionarPaletasCentro();
            posicionarPelotaCentro();
            paletasCentradas = true;
        }


        g.setColor(java.awt.Color.WHITE);
        g.setFont(new java.awt.Font("pixeled", java.awt.Font.BOLD, tamanoletra = (int) (panelHeight * 0.03)));

        // Marcador
        margen = (int) (panelHeight * 0.045);
        g.drawString(Integer.toString(golesIzquierda), margen, (int) (panelHeight * 0.09));
        g.drawString(Integer.toString(golesDerecha), panelWidth - margen - 40, (int) (panelHeight * 0.09));

        // Temporizador
        String tiempo = formatearTiempo(segundosTotales);
        int anchoTexto = g.getFontMetrics().stringWidth(tiempo);
        g.drawString(tiempo, (panelWidth - anchoTexto) / 2 + 10 - 5, (int) (panelHeight * 0.09));

        // Paletas
        if (paletaRoja != null) {
            g.drawImage(paletaRoja, separacion, y1, rectWidth, rectHeight, null);
        }

        if (paletaAzul != null) {
            g.drawImage(paletaAzul, panelWidth - rectWidth - separacion, y2, rectWidth, rectHeight, null);
        }

        // Pelota
        if (pelota != null) {
            g.drawImage(pelota, pelotaX, pelotaY, pelotaSize, pelotaSize, null);
        }
    }
}



