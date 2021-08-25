$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	$("#publishModal").modal("hide");

	// 发送AJAX请求之前，将 CSRF 令牌设置到请求的消息头中
	// var token = $("meta[name='_csrf']").attr("content");
	// var header = $("meta[name='_csrf_header']").attr("content");
	// $(document).ajaxSend(function (e, xhr, options) {
	// 	xhr.setRequestHeader(header, token);
    // });

	// 获取标题和内容
	var title = $("#recipient-name").val(); // JQuery id选择器，.val() 获取文本框的值
	var content = $("#message-text").val();
	// 发送异步请求（POST）
	$.post(
		CONTEXT_PATH + "/discuss/add",
		{"title":title,"content":content},
		function (data) {
            data = $.parseJSON(data); // 将JSON字符串转为JQuery对象
			// 在提示框中显示返回消息
			$("#hintBody").text(data.msg);
			// 显示提示框
			$("#hintModal").modal("show");
			// 2秒后自动隐藏提示框
			setTimeout(function(){
				$("#hintModal").modal("hide");
				// 若发布成功则刷新页面显示更新后的帖子
				if (data.code == 0) {
                    window.location.reload();
				}
			}, 2000);
        }
    );
}