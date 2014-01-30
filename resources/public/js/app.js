;(function($){
  $(function(){
    $("#extract-btn").click(function(){
      var url = $("#url-field").val();
      var encodedUrl=null;
      if(url && url.length>0) {
        encodedUrl = encodeURIComponent(url);
      }
      if(encodedUrl) {
        $.getJSON("/summarize/"+encodedUrl,function(tags){
          var $tagsHolder = $("#tags-holder");
          if(tags.length>0) {
            $tagsHolder.html("");
            for(var t in tags) {
              $tagsHolder.append('<span class="label label-info">'+tags[t]+'</span>');
            }
            $('#url-field').val("");
          } else {
            $tagsHolder.html("We are getting to that url shortly. Check back in a few.");
          }
        })
      }
    })
  })
})(jQuery);
