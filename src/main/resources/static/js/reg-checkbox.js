function setAdmin(){
    let check = document.getElementById('admin').checked;
    if(check){
        document.getElementById('mainModerator').disabled = true;
        if(document.getElementById('moderator') != null){
            document.getElementById('moderator').disabled = true;
        }
        document.getElementById('publisher').disabled = true;
    }else{
        document.getElementById('mainModerator').disabled = false;
        if(document.getElementById('moderator') != null) {
            document.getElementById('moderator').disabled = false;
        }
        document.getElementById('publisher').disabled = false;
    }
}

function setMainModerator(){
    let check = document.getElementById('mainModerator').checked;
    if(check){
        document.getElementById('admin').disabled = true;
        if(document.getElementById('moderator') != null){
            document.getElementById('moderator').disabled = true;
        }
        document.getElementById('publisher').disabled = true;
    }else{
        document.getElementById('admin').disabled = false;
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
        document.getElementById('mainModerator').disabled = true;
        document.getElementById('publisher').disabled = true;
        document.getElementById('hidden-block').hidden = false;
    }else{
        if(!document.getElementById('publisher').checked) {
            document.getElementById('admin').disabled = false;
        }
        document.getElementById('mainModerator').disabled = false;
        document.getElementById('publisher').disabled = false;
        document.getElementById('hidden-block').hidden = true;
    }
}

function setPublisher(){
    let check = document.getElementById('publisher').checked;
    if(check){
        document.getElementById('admin').disabled = true;
        document.getElementById('mainModerator').disabled = true;
        document.getElementById('moderator').disabled = true;
    }else{
        if(document.getElementById('moderator') != null) {
            if (!document.getElementById('moderator').checked) {
                document.getElementById('admin').disabled = false;
            }
            document.getElementById('mainModerator').disabled = false;
            document.getElementById('moderator').disabled = false;
        }else{
            document.getElementById('admin').disabled = false;
            document.getElementById('mainModerator').disabled = false;
            document.getElementById('moderator').disabled = false;
        }
    }
}
function setUntested(){
    let untestedPosts = [[${posts}]];
    console.log(untestedPosts);
    $("#currentPosts").html('');
    for(let i=0; i<untestedPosts.length; i++){
        $("#currentPosts").append(
        '<div class="flex-element border target" id="cp' + i + '">\n' +
        '         <div class="row-100-block text-center" style="background: darkgreen">\n' +
        '             <p style="color: white">прошел первичную проверку</p>\n' +
        '         </div>\n' +
        '     <a class="link-decoration" href="/material/edit/post/' + untestedPosts[i].id + '">\n' +
        '         <div class="row-100-block text-center">\n' +
        '             <h4>' + untestedPosts[i].name + '</h4>\n' +
        '         </div>\n' +
        '         <div class="row-100-block">\n' +
        '             <img class="image" src="/download/' + untestedPosts[i].imageId + '">\n' +
        '         </div>\n' +
        '         <div class="row-100-block flex-grow">\n' +
        '             <p>' + untestedPosts[i].annotation + '</p>\n' +
        '         </div>\n' +
        '     </a>\n' +
        ' </div>'
        );
    }
}
function setVerified(){
    let verifiedPosts = [[${postsAllowed}]]
    console.log(verifiedPosts)
    $("#currentPosts").html('');
    for(let i=0; i<verifiedPosts.length; i++){
        $("#currentPosts").append(
        '<div class="flex-element border target" id="cp' + i + '">\n' +
        '     <a class="link-decoration" href="/material/edit/post/' + verifiedPosts[i].id + '">\n' +
        '         <div class="row-100-block text-center">\n' +
        '             <h4>' + verifiedPosts[i].name + '</h4>\n' +
        '         </div>\n' +
        '         <div class="row-100-block">\n' +
        '             <img class="image" src="/download/' + verifiedPosts[i].imageId + '">\n' +
        '         </div>\n' +
        '         <div class="row-100-block flex-grow">\n' +
        '             <p>' + verifiedPosts[i].annotation + '</p>\n' +
        '         </div>\n' +
        '     </a>\n' +
        ' </div>'
        );
    }
}