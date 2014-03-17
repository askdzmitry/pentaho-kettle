<html>
  <body style="width:100%;height:100%">
    <table border="1" cellspacing="0" cellpadding="0" style="width:100%;height:100%">
        <tr>
          <th>Type</th>
          <th>ID</th>
          <th>Name</th>
          <th>Source file</th>

        </th>
        $views: {view |
          $if(view.result.ok)$
          <tr bgcolor="#66FFCC" >
          $else$
          <tr bgcolor="#FF6666" >
          $endif$
            <td>
              $view.result.item.type$
            </td> 
            <td>
              $view.result.item.id$
            </td>
            <td>
              $view.result.item.name$
            </td>
            <td>
              <a href = "file:///$view.result.item.sourceFile$">$view.result.item.sourceFile$</a>
            </td>
            <td>
              <a href = "file:///$view.comparisonFile$">Compare XMLs</a>
            </td>
          <tr>
        }$
      <tr>
    </table>
  </body>
</html>