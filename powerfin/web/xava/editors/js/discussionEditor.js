if (discussionEditor == null) var discussionEditor = {};

discussionEditor.postMessage = function(application, module, discussionId) {
	var newComment = $('#xava_new_comment_' + discussionId);
	var comments = $('#xava_comments_' + discussionId);
	var lastComment = comments.children().last(); 
	var template = lastComment.clone();
	var commentContent = newComment.val();
	lastComment.find(".ox-discussion-comment-content").html(commentContent);
	lastComment.slideDown(); 
	newComment.val("");
	Discussion.postComment(application, module, discussionId, commentContent);
	comments.append(template);
}

