const data = {
  username: 'raddan',
  password: 'raddan'
};

const xhr = new XMLHttpRequest();
xhr.open('POST', 'http://localhost:8080/login', true);
xhr.setRequestHeader('Content-Type', 'application/json');

xhr.onreadystatechange = function() {
  if (xhr.readyState === 4 && xhr.status === 200) {
    console.log('Успешно отправлено!');
    console.log(xhr.responseText);
  }
};
xhr.send(JSON.stringify(data));
