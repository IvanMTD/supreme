function setAdmin(){
    let check = document.getElementById('admin').checked;
    if(check){
        if(document.getElementById('moderator') != null){
            document.getElementById('moderator').disabled = true;
        }
        document.getElementById('publisher').disabled = true;
    }else{
        if(document.getElementById('moderator') != null) {
            document.getElementById('moderator').disabled = false;
        }
        document.getElementById('publisher').disabled = false;
    }
}
function setModerator(){
    let check = document.getElementById('moderator').checked;
    if(check){
        document.getElementById('admin').disabled = true;
        document.getElementById('hidden-block').hidden = false;
    }else{
        if(!document.getElementById('publisher').checked) {
            document.getElementById('admin').disabled = false;
        }
        document.getElementById('hidden-block').hidden = true;
    }
}
function setPublisher(){
    let check = document.getElementById('publisher').checked;
    if(check){
        document.getElementById('admin').disabled = true;
    }else{
        if(document.getElementById('moderator') != null) {
            if (!document.getElementById('moderator').checked) {
                document.getElementById('admin').disabled = false;
            }
        }else{
            document.getElementById('admin').disabled = false;
        }
    }
}