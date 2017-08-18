/**
* (C) Copyright IBM Corporation 2016.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package jaxrs.resources;

import io.swagger.oas.annotations.Operation;
import io.swagger.oas.annotations.Parameter;
import io.swagger.oas.annotations.parameters.RequestBody;
import io.swagger.oas.annotations.media.Content;
import io.swagger.oas.annotations.media.Schema;
import io.swagger.oas.annotations.responses.ApiResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import jaxrs.model.Booking;

@Path("/bookings")

	public class BookingResource {
	private Map<Integer, Booking> bookings = new ConcurrentHashMap<Integer, Booking>();
	private volatile int currentId = 0;
	
	@GET
	@Operation(summary="Retrieve all bookings for current user", responses={
			@ApiResponse(content=@Content(schema=@Schema(type="array",implementation=Booking.class)))
	})
	@Produces("application/json")
	public Response getBookings(){
		return Response.ok().entity(bookings.values()).build();
	}
	
	@POST
	@Operation(
			summary="Create a booking",
			responses={
					@ApiResponse(
							responseCode="201",
							description="Booking created",
							content = @Content(
									schema=@Schema(name= "id", description = "id of the new booking",type="string")))
					  },
			requestBody = @RequestBody(
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = Booking.class)),
					required = true,
					description = "example booking to add"
					)
			)
	@Consumes("application/json")
	@Produces("application/json")
	public Response createBooking(
			@Parameter(
					description = "booking to create",
					required = true,
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = Booking.class))) 
			Booking task) {
		bookings.put(currentId, task);
		return Response.status(Status.CREATED).entity("{\"id\":" + currentId++ + "}").build();	
	}
	
	@GET
	@Path("{id}")
	@Produces("application/json")
	//@ApiOperation(value="Get a booking with ID")
	@Operation(summary="Get a booking with ID", responses={
			@ApiResponse(responseCode="200", description="Booking retrieved", content=@Content(schema=@Schema(implementation=Booking.class))),
			@ApiResponse(responseCode="404", description="Booking not found")
	})
	//@ApiResponses({
		//@ApiResponse(code = 200, message= "Booking retrieved", response=Booking.class),
		//@ApiResponse(code = 404, message = "Booking not found")})
	public Response getBooking(
			@Parameter(
					name = "id",
					description = "ID of the booking",
					required = true) @PathParam("id") int id){
		Booking booking = bookings.get(id);
		if(booking!=null){
			return Response.ok().entity(booking).build();	
		}
		else{
			
			return Response.status(Status.NOT_FOUND).build();	
		}
	}
	
	@PUT
	@Path("{id}")
	//@ApiOperation(value="Update a booking with ID")
	@Consumes("application/json")
	@Produces("text/plain")
	
	@Operation(summary="Update a booking with ID", responses={
			@ApiResponse(responseCode="200", description="Booking updated"),
			@ApiResponse(responseCode="404", description="Booking not found")
	})
	
	//@ApiResponses({
		//@ApiResponse(code = 200, message= "Booking updated"),
		//@ApiResponse(code = 404, message = "Booking not found")})
	public Response updateBooking(@PathParam("id") int id, Booking booking){
		if(bookings.get(id)!=null){
			bookings.put(id, booking);
			return Response.ok().build();	
		}
		else{
			return Response.status(Status.NOT_FOUND).build();	
		}		
	}
	
	@DELETE
	@Path("{id}")
	//@ApiOperation(value="Delete a booking with ID")
	@Operation(summary="Delete a booking with ID", responses={
			@ApiResponse(responseCode="200", description="Booking deleted"),
			@ApiResponse(responseCode="404", description="Booking not found")
	})
	//@ApiResponses({
		//@ApiResponse(code = 200, message= "Booking deleted"),
		//@ApiResponse(code = 404, message = "Booking not found")})
	@Produces("text/plain")
	public Response deleteBooking(@PathParam("id") int id){
		if(bookings.get(id)!=null) {
			bookings.remove(id);
			return Response.ok().build();
		}
		else {
			return Response.status(Status.NOT_FOUND).build();
		}
	}
}