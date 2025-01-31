<%--

       Copyright 2010-2023 the original author or authors.

       Licensed under the Apache License, Version 2.0 (the "License");
       you may not use this file except in compliance with the License.
       You may obtain a copy of the License at

          https://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing, software
       distributed under the License is distributed on an "AS IS" BASIS,
       WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
       See the License for the specific language governing permissions and
       limitations under the License.

--%>
<%@ include file="../common/IncludeTop.jsp"%>

<div id="Catalog"><form action="/order/newOrder" method="post">

	<table>
		<tr>
			<th colspan=2>Payment Details</th>
		</tr>
		<tr>
			<td>Card Type:</td>
			<td><select name="order.cardType">
				<option value="Visa" <%= "Visa".equals(order.getCardType()) ? "selected" : "" %>>Visa</option>
				<option value="MasterCard" <%= "MasterCard".equals(order.getCardType()) ? "selected" : "" %>>MasterCard</option>
				<option value="American Express" <%= "American Express".equals(order.getCardType()) ? "selected" : "" %>>American Express</option>
			</select></td>
		</tr>
		<tr>
			<td>Card Number:</td>
			<td><input type="text" name="creditCard" value="<%= order.getCreditCard() %>"> * Use a fake
			number!</td>
		</tr>
		<tr>
			<td>Expiry Date (MM/YYYY):</td>
			<td><input type="text" name="expiryDate" value="<%= order.getExpiryDate() %>"></td>
		</tr>
		<tr>
			<th colspan=2>Billing Address</th>
		</tr>

		<tr>
			<td>First name:</td>
			<td><input type="text" name="billToFirstName" value="<%= order.getBillToFirstName() %>"></td>
		</tr>
		<tr>
			<td>Last name:</td>
			<td><input type="text" name="billToLastName" value="<%= order.getBillToLastName() %>"></td>
		</tr>
		<tr>
			<td>Address 1:</td>
			<td><input type="text" name="billAddress1" value="<%= order.getBillAddress1() %>"></td>
		</tr>
		<tr>
			<td>Address 2:</td>
			<td><input type="text" name="billAddress2" value="<%= order.getBillAddress2() %>"></td>
		</tr>
		<tr>
			<td>City:</td>
			<td><input type="text" name="billCity" value="<%= order.getBillCity() %>"></td>
		</tr>
		<tr>
			<td>State:</td>
			<td><input type="text" name="billState" value="<%= order.getBillState() %>"></td>
		</tr>
		<tr>
			<td>Zip:</td>
			<td><input type="text" name="billZip" value="<%= order.getBillZip() %>"></td>
		</tr>
		<tr>
			<td>Country:</td>
			<td><input type="text" name="billCountry" value="<%= order.getBillCountry() %>"></td>
		</tr>

		<tr>
			<td colspan=2><input type="checkbox" name="shippingAddressRequired">
			Ship to different address...</td>
		</tr>

	</table>
	<input type="submit" name="newOrder" value="Continue">

</form></div>

<%@ include file="../common/IncludeBottom.jsp"%>
