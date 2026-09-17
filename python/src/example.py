import folium
import adsbService

map_osm = folium.Map(location=[53.3147, -113.5277], zoom_start=13)

#  Superimpose customized dots (lat, long)
coordinates = []
aircraftData = adsbService.getAircraftInArea("53.3147", "-113.5277", 25)

for ac in aircraftData["ac"]:
    coordinates.append((
        ac["lat"],
        ac["lon"],
        ac["desc"]
    ))

# coordinates = [
#     (41.8781, -87.6298, "Dot A"),
#     (41.8881, -87.6350, "Dot B"),
#     (41.8681, -87.6200, "Dot C"),
# ]

for lat, lon, label in coordinates:
    folium.CircleMarker(
        location=[lat, lon],
        radius=6,  # Size of the dot
        color="crimson",  # Border color
        fill=True,
        fill_color="red",  # Interior color
        fill_opacity=0.8,
        popup=label,  # Optional click popup
    ).add_to(map_osm)

# 3. Save to an HTML file to open in any browser (or render directly in Jupyter)
map_osm.save("map_with_dots.html")