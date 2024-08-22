valid_body={
    "building_limits": {
        "type": "FeatureCollection",
        "features": [
            {
                "type": "Feature",
                "properties": {},
                "geometry": {
                    "type": "Polygon",
                    "coordinates": [
                        [
                            [0.0, 0.0],
                            [10.0, 0.0],
                            [10.0, 10.0],
                            [0.0, 10.0],
                            [0.0, 0.0]
                        ]
                    ]
                }
            }
        ]
    },
    "height_plateaus": {
        "type": "FeatureCollection",
        "features": [
            {
                "type": "Feature",
                "properties": {
                    "elevation": 5.0
                },
                "geometry": {
                    "type": "Polygon",
                    "coordinates": [
                        [
                            [0.0, 0.0],
                            [5.0, 0.0],
                            [5.0, 10.0],
                            [0.0, 10.0],
                            [0.0, 0.0]
                        ]
                    ]
                }
            },
            {
                "type": "Feature",
                "properties": {
                    "elevation": 3.0
                },
                "geometry": {
                    "type": "Polygon",
                    "coordinates": [
                        [
                            [5.0, 0.0],
                            [11.0, 0.0],
                            [11.0, 11.0],
                            [5.0, 11.0],
                            [5.0, 0.0]
                        ]
                    ]
                }
            }
        ]

    }
}

inaccurate_height_plateaus_body={
    "building_limits": {
        "type": "FeatureCollection",
        "features": [
            {
                "type": "Feature",
                "properties": {},
                "geometry": {
                    "type": "Polygon",
                    "coordinates": [
                        [
                            [0.0, 0.0],
                            [10.0, 0.0],
                            [10.0, 10.0],
                            [0.0, 10.0],
                            [0.0, 0.0]
                        ]
                    ]
                }
            }
        ]
    },
    "height_plateaus": {
        "type": "FeatureCollection",
        "features": [
            {
                "type": "Feature",
                "properties": {
                    "elevation": 5.0
                },
                "geometry": {
                    "type": "Polygon",
                    "coordinates": [
                        [
                            [2.0, 2.0],
                            [8.0, 2.0],
                            [8.0, 5.0],
                            [2.0, 5.0],
                            [2.0, 2.0]
                        ]
                    ]
                }
            },
            {
                "type": "Feature",
                "properties": {
                    "elevation": 3.0
                },
                "geometry": {
                    "type": "Polygon",
                    "coordinates": [
                        [
                            [1.0, 1.0],
                            [9.0, 1.0],
                            [9.0, 9.0],
                            [1.0, 9.0],
                            [1.0, 1.0]
                        ]
                    ]
                }
            }
        ]

    }
}

invalid_polygon_body={
    "building_limits": {
        "type": "FeatureCollection",
        "features": [
            {
                "type": "Feature",
                "properties": {},
                "geometry": {
                    "type": "Polygon",
                    "coordinates": [
                        [
                            [0.0, 0.0],
                            [10.0, 0.0],
                            [10.0, 10.0],
                            [0.0, 10.0],
                            [0.0, 0.0]
                        ]
                    ]
                }
            }
        ]
    },
    "height_plateaus": {
        "type": "FeatureCollection",
        "features": [
            {
                "type": "Feature",
                "properties": {
                    "elevation": 5.0
                },
                "geometry": {
                    "type": "Polygon",
                    "coordinates": [
                        [
                            [0.0, 0.0],
                            [5.0, 0.0],
                            [5.0, 10.0],
                            [0.0, 10.0]
                        ]
                    ]
                }
            },
            {
                "type": "Feature",
                "properties": {
                    "elevation": 3.0
                },
                "geometry": {
                    "type": "Polygon",
                    "coordinates": [
                        [
                            [5.0, 0.0],
                            [11.0, 0.0],
                            [11.0, 11.0],
                            [5.0, 11.0],
                            [5.0, 0.0]
                        ]
                    ]
                }
            }
        ]

    }
}

invalid_body={
    "building_limits": {
        "type": "FeatureCollection",
        "features": [
            {
                "type": "Feature",
                "properties": {},
                "geometry": {
                    "type": "Polygon",
                    "coordinates": [
                        [
                            [0.0, 0.0],
                            [10.0, 0.0],
                            [10.0, 10.0],
                            [0.0, 10.0],
                            [0.0, 0.0]
                        ]
                    ]
                }
            }
        ]
    },
    "height_plateaus": {
        "invalid_property": 0
    }
}