// Найти элемент с id "commits"
var commitsContainer = document.getElementById("commits");

// Выполнить fetch запрос к API GitHub
fetch("https://api.github.com/repos/LencoDigitexer/Shedule_app/commits")
  .then(function(response) {
    return response.json();
  })
  .then(function(commitsData) {
    // Пройти по массиву данных и создать соответствующий HTML код для каждого коммита
    commitsData.forEach(function(commit) {
      // Создать элемент div для коммита
      var commitDiv = document.createElement("div");
      commitDiv.className = "col-lg-6 col-lg-offset-3";

      // Преобразовать дату и время в формат "DD:MM:YYYY"
      var commitDate = new Date(commit.commit.author.date);
      var formattedDate = commitDate.getDate().toString().padStart(2, '0') + "." +
                         (commitDate.getMonth() + 1).toString().padStart(2, '0') + "." +
                         commitDate.getFullYear();

      // Создать заголовок h3 с ссылкой на html_url и отформатированной датой
      var commitLink = document.createElement("a");
      commitLink.href = commit.html_url;
      commitLink.textContent = commit.sha.substring(0, 7); // Взять первые 7 символов из sha
      var commitDateSpan = document.createElement("span");
      commitDateSpan.textContent = " (" + formattedDate + ")";
      var commitTitle = document.createElement("h3");
      commitTitle.style.color = "rgb(244, 244, 244)";
      commitTitle.style.fontFamily = "Times New Roman";
      commitTitle.style.letterSpacing = "normal";
      commitTitle.style.textAlign = "start";
      commitTitle.appendChild(commitLink);
      commitTitle.appendChild(commitDateSpan);

      // Создать абзац с сообщением коммита
      var commitMessage = document.createElement("p");
      commitMessage.style.color = "rgb(244, 244, 244)";
      commitMessage.style.fontFamily = "Times New Roman";
      commitMessage.style.textAlign = "start";
      commitMessage.textContent = commit.commit.message;

      // Добавить заголовок и сообщение к элементу коммита
      commitDiv.appendChild(commitTitle);
      commitDiv.appendChild(commitMessage);

      // Добавить элемент коммита к контейнеру "commits"
      commitsContainer.appendChild(commitDiv);
    });
  })
  .catch(function(error) {
    console.error("Ошибка при загрузке данных:", error);
  });
