package axx;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import javax.swing.*;
import java.awt.*;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class QrCodeGenerator {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(QrCodeGenerator::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("QR Code Generator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // 顶部面板，包含输入文本框和生成按钮
        JPanel topPanel = new JPanel(new BorderLayout());
        JTextArea textArea = new JTextArea();
        JButton generateButton = new JButton("Generate QR Code");

        generateButton.addActionListener(e -> {
            String text = textArea.getText();
            try {
                // 生成二维码
                ImageIcon qrCodeIcon = generateQRCode(text);

                // 弹出对话框展示二维码
                showQRCodeDialog(frame, qrCodeIcon);
            } catch (WriterException | IOException ex) {
                ex.printStackTrace();
            }
        });

        topPanel.add(new JScrollPane(textArea), BorderLayout.CENTER);
        topPanel.add(generateButton, BorderLayout.SOUTH);

        mainPanel.add(topPanel, BorderLayout.CENTER);

        frame.getContentPane().add(mainPanel);
        frame.setSize(400, 300);
        centerFrameOnScreen(frame);  // 初始窗口居中显示
        frame.setVisible(true);
    }

    private static ImageIcon generateQRCode(String text) throws WriterException, IOException {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 200, 200, hints);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

        // 将输出流的内容转换成 byte 数组
        byte[] imageBytes = outputStream.toByteArray();

        // 创建 ImageIcon
        return new ImageIcon(imageBytes);
    }

    private static void showQRCodeDialog(JFrame parentFrame, ImageIcon qrCodeIcon) {
        // 创建对话框
        JDialog dialog = new JDialog(parentFrame, "Generated QR Code", true);

        // 设置对话框布局
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(qrCodeIcon);
        panel.add(label, BorderLayout.CENTER);
        dialog.add(panel);

        // 设置对话框初始大小
        int initialSize = 250;
        dialog.setSize(initialSize, initialSize);

        // 设置对话框居中
        centerFrameOnScreen((Window) dialog);

        // 添加组件监听器，以便在对话框大小调整时更新二维码图片大小
        dialog.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // 获取对话框的新大小（取最小值保持正方形）
                int newSize = Math.min(dialog.getWidth(), dialog.getHeight());

                // 调整图片大小并更新标签
                ImageIcon resizedIcon = resizeImageIcon(qrCodeIcon, newSize, newSize);
                label.setIcon(resizedIcon);
            }
        });

        // 显示对话框
        dialog.setVisible(true);
    }

    private static ImageIcon resizeImageIcon(ImageIcon icon, int width, int height) {
        Image image = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(image);
    }

    private static void centerFrameOnScreen(Window frame) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - frame.getWidth()) / 2;
        int y = (screenSize.height - frame.getHeight()) / 2;
        frame.setLocation(x, y);
    }
}