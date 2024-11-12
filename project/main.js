// Music Player State
const playerState = {
  isPlaying: false,
  currentTrack: 0,
  volume: 100
};

// DOM Elements
const playBtn = document.getElementById('playBtn');
const prevBtn = document.getElementById('prevBtn');
const nextBtn = document.getElementById('nextBtn');
const volumeSlider = document.getElementById('volumeSlider');
const progress = document.getElementById('progress');
const currentTimeEl = document.getElementById('currentTime');
const durationEl = document.getElementById('duration');
const playlistItems = document.getElementById('playlistItems');

// Playlist data (mock)
const playlist = [
  { title: 'Song 1', artist: 'Artist 1', duration: '3:45' },
  { title: 'Song 2', artist: 'Artist 2', duration: '4:20' },
  { title: 'Song 3', artist: 'Artist 3', duration: '3:30' }
];

// Event Listeners
playBtn.addEventListener('click', togglePlay);
prevBtn.addEventListener('click', playPrevious);
nextBtn.addEventListener('click', playNext);
volumeSlider.addEventListener('input', handleVolumeChange);

// Functions
function togglePlay() {
  playerState.isPlaying = !playerState.isPlaying;
  updatePlayButton();
  if (playerState.isPlaying) {
    // Simulate playing animation
    startProgressAnimation();
  }
}

function updatePlayButton() {
  const icon = playBtn.querySelector('i');
  icon.className = playerState.isPlaying ? 'fas fa-pause' : 'fas fa-play';
}

function playPrevious() {
  playerState.currentTrack = (playerState.currentTrack - 1 + playlist.length) % playlist.length;
  updateTrackInfo();
}

function playNext() {
  playerState.currentTrack = (playerState.currentTrack + 1) % playlist.length;
  updateTrackInfo();
}

function handleVolumeChange(e) {
  playerState.volume = e.target.value;
  // Update volume logic would go here
}

function updateTrackInfo() {
  const track = playlist[playerState.currentTrack];
  document.getElementById('trackTitle').textContent = track.title;
  document.getElementById('artistName').textContent = track.artist;
  document.getElementById('duration').textContent = track.duration;
  
  // Update playlist active item
  const items = playlistItems.getElementsByTagName('li');
  Array.from(items).forEach((item, index) => {
    item.classList.toggle('active', index === playerState.currentTrack);
  });
}

function startProgressAnimation() {
  let width = 0;
  const interval = setInterval(() => {
    if (!playerState.isPlaying || width >= 100) {
      clearInterval(interval);
      if (width >= 100) {
        playNext();
        width = 0;
        if (playerState.isPlaying) startProgressAnimation();
      }
      return;
    }
    width += 0.1;
    progress.style.width = `${width}%`;
    updateCurrentTime(width);
  }, 100);
}

function updateCurrentTime(progress) {
  const duration = 225; // 3:45 in seconds
  const currentSeconds = Math.floor((progress / 100) * duration);
  const minutes = Math.floor(currentSeconds / 60);
  const seconds = currentSeconds % 60;
  currentTimeEl.textContent = `${minutes}:${seconds.toString().padStart(2, '0')}`;
}

// Initialize player
updateTrackInfo();