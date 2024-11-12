import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class MusicPlayer extends JFrame {
    private boolean isPlaying = false;
    private int currentTrack = 0;
    private int volume = 100;
    private Timer progressTimer;
    private int progress = 0;

    // UI Components
    private JLabel albumArt;
    private JLabel trackTitle;
    private JLabel artistName;
    private JProgressBar progressBar;
    private JLabel currentTimeLabel;
    private JLabel durationLabel;
    private JButton playButton;
    private JButton prevButton;
    private JButton nextButton;
    private JSlider volumeSlider;
    private JList<String> playlist;
    private DefaultListModel<String> playlistModel;

    // Mock data
    private ArrayList<Track> tracks;

    public MusicPlayer() {
        setTitle("Music Player");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(400, 600));
        setupUI();
        initializeMockData();
        pack();
        setLocationRelativeTo(null);
    }

    private void setupUI() {
        // Set dark theme colors
        setBackground(new Color(26, 26, 26));
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(44, 62, 80));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JLabel headerLabel = new JLabel("Music Player");
        headerLabel.setFont(new Font("Inter", Font.BOLD, 24));
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(headerLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Album Art
        albumArt = new JLabel(new ImageIcon("placeholder.png"));
        albumArt.setPreferredSize(new Dimension(250, 250));
        albumArt.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(albumArt);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Track Info
        trackTitle = new JLabel("Track Title");
        trackTitle.setFont(new Font("Inter", Font.BOLD, 18));
        trackTitle.setForeground(Color.WHITE);
        trackTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(trackTitle);

        artistName = new JLabel("Artist Name");
        artistName.setFont(new Font("Inter", Font.PLAIN, 14));
        artistName.setForeground(new Color(189, 195, 199));
        artistName.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(artistName);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Progress Bar Panel
        JPanel progressPanel = new JPanel(new BorderLayout(5, 0));
        progressPanel.setOpaque(false);
        
        progressBar = new JProgressBar(0, 100);
        progressBar.setPreferredSize(new Dimension(350, 5));
        progressBar.setBackground(new Color(255, 255, 255, 26));
        progressBar.setForeground(new Color(52, 152, 219));
        
        currentTimeLabel = new JLabel("0:00");
        currentTimeLabel.setForeground(new Color(189, 195, 199));
        durationLabel = new JLabel("3:45");
        durationLabel.setForeground(new Color(189, 195, 199));

        progressPanel.add(currentTimeLabel, BorderLayout.WEST);
        progressPanel.add(progressBar, BorderLayout.CENTER);
        progressPanel.add(durationLabel, BorderLayout.EAST);
        
        mainPanel.add(progressPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Controls Panel
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        controlsPanel.setOpaque(false);

        prevButton = createControlButton("⏮");
        playButton = createControlButton("▶");
        nextButton = createControlButton("⏭");

        controlsPanel.add(prevButton);
        controlsPanel.add(playButton);
        controlsPanel.add(nextButton);
        
        mainPanel.add(controlsPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Volume Control
        JPanel volumePanel = new JPanel(new BorderLayout(10, 0));
        volumePanel.setOpaque(false);
        
        JLabel volumeIcon = new JLabel("🔊");
        volumeIcon.setForeground(Color.WHITE);
        volumeSlider = new JSlider(0, 100, 100);
        volumeSlider.setOpaque(false);
        
        volumePanel.add(volumeIcon, BorderLayout.WEST);
        volumePanel.add(volumeSlider, BorderLayout.CENTER);
        mainPanel.add(volumePanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Playlist
        JPanel playlistPanel = new JPanel(new BorderLayout());
        playlistPanel.setOpaque(false);
        playlistPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(52, 152, 219)),
            "Playlist",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            null,
            Color.WHITE
        ));

        playlistModel = new DefaultListModel<>();
        playlist = new JList<>(playlistModel);
        playlist.setBackground(new Color(0, 0, 0, 51));
        playlist.setForeground(Color.WHITE);
        playlist.setSelectionBackground(new Color(52, 152, 219));
        playlist.setSelectionForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(playlist);
        scrollPane.setPreferredSize(new Dimension(350, 150));
        playlistPanel.add(scrollPane);
        
        mainPanel.add(playlistPanel);

        // Add event listeners
        setupEventListeners();

        // Add main panel to frame
        add(mainPanel);
    }

    private JButton createControlButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Inter", Font.PLAIN, 20));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(52, 152, 219));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(50, 50));
        return button;
    }

    private void setupEventListeners() {
        playButton.addActionListener(e -> togglePlay());
        prevButton.addActionListener(e -> playPrevious());
        nextButton.addActionListener(e -> playNext());
        volumeSlider.addChangeListener(e -> handleVolumeChange());
        playlist.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                currentTrack = playlist.getSelectedIndex();
                if (currentTrack != -1) {
                    updateTrackInfo();
                }
            }
        });
    }

    private void initializeMockData() {
        tracks = new ArrayList<>();
        tracks.add(new Track("Song 1", "Artist 1", "3:45"));
        tracks.add(new Track("Song 2", "Artist 2", "4:20"));
        tracks.add(new Track("Song 3", "Artist 3", "3:30"));

        // Populate playlist
        for (Track track : tracks) {
            playlistModel.addElement(track.title + " - " + track.artist);
        }
        
        updateTrackInfo();
    }

    private void togglePlay() {
        isPlaying = !isPlaying;
        playButton.setText(isPlaying ? "⏸" : "▶");
        
        if (isPlaying) {
            startProgressAnimation();
        } else if (progressTimer != null) {
            progressTimer.stop();
        }
    }

    private void playPrevious() {
        currentTrack = (currentTrack - 1 + tracks.size()) % tracks.size();
        updateTrackInfo();
    }

    private void playNext() {
        currentTrack = (currentTrack + 1) % tracks.size();
        updateTrackInfo();
    }

    private void handleVolumeChange() {
        volume = volumeSlider.getValue();
        // Volume logic would go here
    }

    private void updateTrackInfo() {
        Track track = tracks.get(currentTrack);
        trackTitle.setText(track.title);
        artistName.setText(track.artist);
        durationLabel.setText(track.duration);
        playlist.setSelectedIndex(currentTrack);
        progress = 0;
        progressBar.setValue(0);
    }

    private void startProgressAnimation() {
        if (progressTimer != null) {
            progressTimer.stop();
        }

        progressTimer = new Timer(100, e -> {
            progress++;
            if (progress >= 100) {
                progress = 0;
                playNext();
            }
            progressBar.setValue(progress);
            updateCurrentTime();
        });
        progressTimer.start();
    }

    private void updateCurrentTime() {
        int duration = 225; // 3:45 in seconds
        int currentSeconds = (int) ((progress / 100.0) * duration);
        int minutes = currentSeconds / 60;
        int seconds = currentSeconds % 60;
        currentTimeLabel.setText(String.format("%d:%02d", minutes, seconds));
    }

    private static class Track {
        String title;
        String artist;
        String duration;

        Track(String title, String artist, String duration) {
            this.title = title;
            this.artist = artist;
            this.duration = duration;
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new MusicPlayer().setVisible(true);
        });
    }
}