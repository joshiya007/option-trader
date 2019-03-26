<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="org.json.JSONArray"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>FT</title>
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/css/bootstrap.min.css">
<link rel="stylesheet"
	href="https://cdn.datatables.net/1.10.19/css/jquery.dataTables.min.css">
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/js/bootstrap.min.js"></script>
<script
	src="https://cdn.datatables.net/1.10.19/js/jquery.dataTables.min.js"></script>
</head>
<body>
	<%
		JSONArray data = new JSONArray();
		data = (JSONArray) request.getAttribute("ftdata");
	%>
	<table id="buyTable" class="display" style="width: 100%;">
		<thead>
			<tr>
				<th>Script</th>
				<th>Lot Size</th>
				<th>Buy Above</th>
				<th>LTP</th>
				<th>Sell Below</th>
				<th>% Change</th>
				<th>Open</th>
				<th>High</th>
				<th>Low</th>
			</tr>
		</thead>
		<tbody>
			<%
				for (int i = 0; i < data.length(); i++) {
					if (data.getJSONObject(i).get("type").toString().equalsIgnoreCase("GAINER")) {
						String bg = "";
						if (data.getJSONObject(i).get("dayOpen").toString()
								.equalsIgnoreCase(data.getJSONObject(i).get("dayLow").toString())) {
							bg = "green";
						}
						float ltp = Float.parseFloat(data.getJSONObject(i).get("ltp").toString());
						float buyAbove = Float.parseFloat(data.getJSONObject(i).get("buyAbove").toString());
						float sellBelow = Float.parseFloat(data.getJSONObject(i).get("sellBelow").toString());
						float dayHigh = Float.parseFloat(data.getJSONObject(i).get("dayHigh").toString());
						float dayLow = Float.parseFloat(data.getJSONObject(i).get("dayLow").toString());
						
						String middle = "";
						if( (dayHigh < buyAbove) && (dayLow > sellBelow)){
							middle = "yellow";
						}
						
			%>
			<tr <%if (bg.equalsIgnoreCase("green")) {%> style="background: lightgreen"<%}%> >

				<td><%=data.getJSONObject(i).get("symbol")%>
				<td><%=data.getJSONObject(i).get("lotSize")%>
				<td <%if (middle.equalsIgnoreCase("yellow")) {%> style="background: yellow"<%}%>  ><%=data.getJSONObject(i).get("buyAbove")%>
				<td><%=data.getJSONObject(i).get("ltp")%>
				<td><%=data.getJSONObject(i).get("sellBelow")%>
				<td><%=data.getJSONObject(i).get("percentageChange")%>
				<td><%=data.getJSONObject(i).get("dayOpen")%>
				<td><%=data.getJSONObject(i).get("dayHigh")%>
				<td><%=data.getJSONObject(i).get("dayLow")%>
			</tr>

			<%
				}
				}
			%>
		</tbody>
	</table>

	<table id="sellTable" class="display"
		style="width: 100%; margin-top: 20px">
		<thead>
			<tr>
				<th>Script</th>
				<th>Lot Size</th>
				<th>Buy Above</th>
				<th>LTP</th>
				<th>Sell Below</th>
				<th>% Change</th>
				<th>Open</th>
				<th>High</th>
				<th>Low</th>
			</tr>
		</thead>
		<tbody>
			<%
				for (int i = 0; i < data.length(); i++) {
					if (data.getJSONObject(i).get("type").toString().equalsIgnoreCase("LOSER")) {
						String bg = "";
						if (data.getJSONObject(i).get("dayOpen").toString()
								.equalsIgnoreCase(data.getJSONObject(i).get("dayHigh").toString())) {
							bg = "red";
						}
			%>
			<tr <%if (bg.equalsIgnoreCase("red")) {%>
				style="background: red; opacity: 0.7; color: white"<%}%> %>
				<td><%=data.getJSONObject(i).get("symbol")%>
				<td><%=data.getJSONObject(i).get("lotSize")%>
				<td><%=data.getJSONObject(i).get("buyAbove")%>
				<td><%=data.getJSONObject(i).get("ltp")%>
				<td><%=data.getJSONObject(i).get("sellBelow")%>
				<td style="color: black"><%=data.getJSONObject(i).get("percentageChange")%>
				<td><%=data.getJSONObject(i).get("dayOpen")%>
				<td><%=data.getJSONObject(i).get("dayHigh")%>
				<td><%=data.getJSONObject(i).get("dayLow")%>
			</tr>

			<%
				}
				}
			%>
		</tbody>
	</table>


	<script type="text/javascript">
		$(document).ready(function() {
			$('#buyTable').DataTable({
				"filter" : false,
				"paging" : false,
				"info" : false,
				"order" : [ [ 5, "ase" ] ]
			});
		});
		$(document).ready(function() {
			$('#sellTable').DataTable({
				"filter" : false,
				"paging" : false,
				"info" : false,
				"order" : [ [ 5, "desc" ] ]
			});
		});
	</script>
</body>
</html>